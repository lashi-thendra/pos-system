const WS_API_BASE_URL = "ws://localhost:8080/pos";
const txtCustomer = $('#txt-customer');
const customerNameElm = $('#customer-name');
let customer = null;
let socket = null;


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