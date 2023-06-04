const WS_API_BASE_URL = "ws://localhost:8080/pos";
const REST_API_BASE_URL = "http://localhost:8080/pos";
const txtCustomer = $('#txt-customer');
const customerNameElm = $('#customer-name');
const itemInfoElm = $('#item-info');
const txtCode = $('#txt-code');
const frmOrder = $('#frm-order');
let customer = null;
let socket = null;
let item = null;


socket = new WebSocket(`${WS_API_BASE_URL}/customer-ws`);

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
        if (itemData.length === 0){
            itemInfoElm.addClass('d-none');
            return;
        }
        item = itemData[0];
        console.log(item);
        description.text(item.description);
        stock.text(item.qty);
        unitPrice.text(item.price);
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