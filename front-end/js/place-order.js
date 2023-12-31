import {Cart} from "./cart.js";
import Big from "../node_modules/big.js/big.mjs";
import {DateTimeFormatter, LocalDateTime} from '../node_modules/@js-joda/core/dist/js-joda.esm.js';
import {showProgressBar, showToast , REST_API_BASE_URL, WS_API_BASE_URL} from "./main.js";

const tbodyElm = $('#tbl-order tbody');
const tFootElm = $("#tbl-order tfoot");
const txtCustomer = $('#txt-customer');
const customerNameElm = $('#customer-name');
const itemInfoElm = $('#item-info');
const txtCode = $('#txt-code');
const frmOrder = $('#frm-order');
const txtQty = $('#txt-qty');
const netTotalElm = $("#net-total");
const btnPlaceOrder = $('#btn-place-order');
const orderDateTimeElm = $("#order-date-time");

let customer = null;
let socket = null;
let item = null;
let cart;
let priceOfSelectedItem;
let quantityOfSelectedItem;

console.log("trying to connect with web socket");
socket = new WebSocket(`ws://localhost:8080/pos/api/v1/customers-ws`);

console.log("connected with web socket");
cart = new Cart((total)=> netTotalElm.text(formatPrice(total)));
updateOrderDetails();

txtCustomer.on('input', () =>{
    txtCustomer.removeClass('is-invalid');
    findCustomer();
});
txtCustomer.on('blur',()=>{
    if(txtCustomer.val().trim() && !customer){
        txtCustomer.addClass('is-invalid');
    }else{
        txtCustomer.removeClass('is-invalid');
    }
});
socket.addEventListener('message',(eventData)=>{
    customer = JSON.parse(eventData.data);
    customerNameElm.text(customer.name);
});
$('#btn-clear-customer').on('click',()=>{
   customer = null;
   customerNameElm.text("walk-in-customer");
   txtCustomer.val("");
   txtCustomer.removeClass("is-invalid");
   txtCustomer.trigger("focus");
});
txtCode.on('input',()=>{itemInfoElm.removeClass('is-invalid')});
txtCode.on('change',()=>{
    let code = txtCode.val().trim();
    if(!code || !/^\d+$/.test(code)){
        txtCode.addClass("is-invalid");
        return;
    }
    txtCode.removeClass("is-invalid");
    findItem(code);
});
frmOrder.on('submit',(eventData)=>{
    eventData.preventDefault();
    
    const stocks = +$("#stock span").text();
    console.log(stocks);
    if(stocks< +txtQty.val() || +txtQty.val() <= 0 ){
        txtQty.addClass('is-invalid');
        txtQty.trigger('select');
        return;
    }

    item.qty = +txtQty.val();
    txtQty.removeClass('is-invalid');

    console.log(item.unitPrice, item.qty)

    if (cart.containItem(item.code)) {
        const codeElm = Array.from(tbodyElm.find("tr td:first-child .code")).find(codeElm => $(codeElm).text() === item.code);
        const qtyElm = $(codeElm).parents("tr").find("td:nth-child(2)");
        const priceElm = $(codeElm).parents("tr").find("td:nth-child(4)");

        cart.updateItemQty(item.code, cart.getItem(item.code).qty + item.qty);
        qtyElm.text(cart.getItem(item.code).qty);
        priceElm.text(formatNumber(Big(cart.getItem(item.code).qty).times(item.unitPrice)));
    } else {
        addItemToTable(item);
        cart.addItem(item);
    }

    itemInfoElm.addClass("d-none");
    frmOrder.addClass("d-none");
    txtCode.val("");
    txtCode.trigger("focus");
    txtQty.val("1");
});
tbodyElm.on('click', 'svg.delete', (eventData) => {
    const trElm = $(eventData.target).parents("tr");
    const code = trElm.find("td:first-child .code").text();
    cart.deleteItem(code);
    trElm.remove();
    txtCode.val("");
    txtCode.trigger('input');
    if (!cart.itemList.length) {
        tFootElm.show();
        txtCode.trigger('focus');
    }
});
btnPlaceOrder.on('click',()=> placeOrder());
setInterval(setDateTime, 1000);


function findCustomer() {
    const id = txtCustomer.val().trim().replace('C','');
    if(!id) return;
    if( !/^\d+$/.test(id)) {
        txtCustomer.addClass('is-invalid');
        return;
    }
    customer = null;
    customerNameElm.text('walk-in-customer');

    if(socket.readyState === socket.OPEN) socket.send(id);
}
function findItem(code){
    const description = $("#description");
    const stock = $("#stock span");
    const unitPrice = $("#unit-price");

    txtCode.attr('disabled',true);
    const jqxhr = $.ajax(`${REST_API_BASE_URL}/items/${code}`);

    jqxhr.done((itemData)=>{
        console.log(itemData)
        if (itemData.length === 0){
            itemInfoElm.addClass('d-none');
            return;
        }
        item = itemData;
        console.log(item);
        description.text(item.description);
        stock.text(item.qty);
        unitPrice.text(formatPrice(item.unitPrice));
        if(item.qty){
            frmOrder.removeClass("d-none");
        }else{
            frmOrder.addClass("d-none");
        }

        itemInfoElm.removeClass('d-none');

    });
    jqxhr.fail(()=>{
        itemInfoElm.addClass("d-none");
    });
    jqxhr.always(()=>{
        txtCode.removeAttr('disabled');
    });
}
export function formatPrice(price) {
    return new Intl.NumberFormat('en-LK', {
        style: 'currency',
        currency: 'LKR',
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    }).format(price);
}
export function formatNumber(number) {
    return new Intl.NumberFormat('en-LK', {
        style: 'decimal',
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    }).format(number);
}
function addItemToTable(item) {
    tFootElm.hide();
    const trElm = $(`<tr>
                    <td>
                        <div class="d-flex justify-content-between align-items-center">
                            <div>
                                <div class="fw-bold code">${item.code}</div>
                                <div>${item.description}</div>
                            </div>
                            <svg data-bs-toggle="tooltip" data-bs-title="Remove Item" xmlns="http://www.w3.org/2000/svg"
                                 width="32" height="32" fill="currentColor" class="bi bi-trash delete"
                                 viewBox="0 0 16 16">
                                <path d="M5.5 5.5A.5.5 0 0 1 6 6v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5Zm2.5 0a.5.5 0 0 1 .5.5v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5Zm3 .5a.5.5 0 0 0-1 0v6a.5.5 0 0 0 1 0V6Z"/>
                                <path d="M14.5 3a1 1 0 0 1-1 1H13v9a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V4h-.5a1 1 0 0 1-1-1V2a1 1 0 0 1 1-1H6a1 1 0 0 1 1-1h2a1 1 0 0 1 1 1h3.5a1 1 0 0 1 1 1v1ZM4.118 4 4 4.059V13a1 1 0 0 0 1 1h6a1 1 0 0 0 1-1V4.059L11.882 4H4.118ZM2.5 3h11V2h-11v1Z"/>
                            </svg>
                        </div>
                    </td>
                    <td>
                        ${item.qty}
                    </td>
                    <td>
                        ${formatNumber(item.unitPrice)}
                    </td>
                    <td>
                        ${formatNumber(Big(item.unitPrice).times(Big(item.qty)))}
                    </td>
                </tr>`);
    tbodyElm.append(trElm);
}
function updateOrderDetails() {
    const id = cart.customer?.id.toString().padStart(3, '0');
    txtCustomer.val(id ? 'C' + id : '');
    customerNameElm.text(cart.customer?.name);
    cart.itemList.forEach(item => addItemToTable(item));
    netTotalElm.text(formatPrice(cart.getTotal()));
}
function setDateTime() {
    const now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    orderDateTimeElm.text(now);
}
function placeOrder(){
    if (!cart.itemList.length) return;

    cart.dateTime = orderDateTimeElm.text();
    btnPlaceOrder.attr('disabled', true);
    const xhr = new XMLHttpRequest();

    console.log(cart);

    const jqxhr = $.ajax(`${REST_API_BASE_URL}/orders`, {
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(cart),
        xhr: ()=> xhr
    });

    showProgressBar(xhr);

    jqxhr.done(()=> {
        cart.clear();
        $("#btn-clear-customer").trigger('click');
        txtCode.val("");
        txtCode.trigger("input");
        tbodyElm.empty();
        tFootElm.show();
        showToast('success', 'Success', 'Order has been placed successfully');
    });
    jqxhr.fail(()=> {
        showToast('error', 'Failed', "Failed to place the order, try again!");
    });
    jqxhr.always(()=> btnPlaceOrder.removeAttr('disabled'));
}