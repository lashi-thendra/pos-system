import {showProgressBar, showToast} from "./main.js";

const tbodyElm = $('#tbl-customers tbody');
const txtSearchItems = $('#txt-search-items');
const txtCode = $("#txt-code");
const txtDesc = $("#txt-description");
const txtQuantity = $("#txt-quantity");
const txtPrice = $("#txt-price");
const btnSaveCustomer = $('#btn-save-item');

txtSearchItems.on("input", ()=>getItems());
btnSaveCustomer.on('click',()=>{

    if (!validateData()) {
        return false;
    }
    const code = txtCode.val().trim();
    const description = txtDesc.val().trim();
    const quantity = txtQuantity.val().trim();
    const price = txtPrice.val().trim();

    let item = {
        description, quantity, price
    };

    const xhr = new XMLHttpRequest();

    xhr.addEventListener('readystatechange', ()=>{
        if(xhr.readyState === 4){
            [txtDesc, txtPrice, txtQuantity, btnSaveCustomer].forEach(elm => elm.removeAttr('disabled'));
            $("#loader").css('visibility', 'hidden');
            if(xhr.status === 201){
                txtSearchItems.val('');
                getItems();
                resetForm(true);
                txtDesc.trigger("focus");
                showToast('success','Saved','Customer has been save successfully');
            }else{
                console.log("working");
                const errObj = JSON.parse(xhr.responseText);
                showToast('error','Failed to save',errObj.message);
            }
        }
    })

    xhr.open('POST','http://localhost:8080/pos/items', true);
    xhr.setRequestHeader('Content-type','application/json');
    showProgressBar(xhr);
    xhr.send(JSON.stringify(item));
    $("#loader").css('visibility', 'visible');

});
tbodyElm.on('click', ".delete", (eventData)=> {

    const code = +$(eventData.target).parents("tr").children("td:first-child").text().replace('C', '');
    const xhr = new XMLHttpRequest();
    const jqxhr = $.ajax(`http://localhost:8080/pos/items/${code}`, {
        method: 'DELETE',
        xhr: ()=> xhr
    });
    showProgressBar(xhr);
    jqxhr.done(()=> {
        showToast('success', 'Deleted', 'Items has been deleted successfully');
        $(eventData.target).tooltip('dispose');
        getItems();
    });
    jqxhr.fail(()=> {
        showToast('error', 'Failed', "Failed to delete the item, try again!");
    });
});

function getItems(){
    const tFoot = $('#tbl-items tfoot tr td:first-child');
    const xhr = new XMLHttpRequest();
    const searchText = $(txtSearchItems).val().trim();
    const query = (searchText)? `?q=${searchText}`: "";

    xhr.addEventListener('readystatechange',()=>{
        if(xhr.readyState === 4){
            if(xhr.status === 200){
                tbodyElm.empty();
                let itemList = JSON.parse(xhr.responseText);
                itemList.forEach((item)=>{
                    tbodyElm.append(`
                    <tr>
                        <td class="text-center">${formatItemCode(item.code)}</td>
                        <td>${item.description}</td>
                        <td class="d-none d-xl-table-cell">${item.price}</td>
                        <td class="quantity text-center">${item.quantity}</td>
                        <td>
                            <div class="actions d-flex gap-3 justify-content-center">
                                <svg data-bs-toggle="tooltip" data-bs-title="Edit Customer" xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor"
                                    class="bi bi-pencil-square edit" viewBox="0 0 16 16">
                                    <path
                                        d="M15.502 1.94a.5.5 0 0 1 0 .706L14.459 3.69l-2-2L13.502.646a.5.5 0 0 1 .707 0l1.293 1.293zm-1.75 2.456-2-2L4.939 9.21a.5.5 0 0 0-.121.196l-.805 2.414a.25.25 0 0 0 .316.316l2.414-.805a.5.5 0 0 0 .196-.12l6.813-6.814z" />
                                    <path fill-rule="evenodd"
                                        d="M1 13.5A1.5 1.5 0 0 0 2.5 15h11a1.5 1.5 0 0 0 1.5-1.5v-6a.5.5 0 0 0-1 0v6a.5.5 0 0 1-.5.5h-11a.5.5 0 0 1-.5-.5v-11a.5.5 0 0 1 .5-.5H9a.5.5 0 0 0 0-1H2.5A1.5 1.5 0 0 0 1 2.5v11z" />
                                </svg>
                                <svg data-bs-toggle="tooltip" data-bs-title="Delete Customer" xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor"
                                    class="bi bi-trash delete" viewBox="0 0 16 16">
                                    <path
                                        d="M5.5 5.5A.5.5 0 0 1 6 6v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5Zm2.5 0a.5.5 0 0 1 .5.5v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5Zm3 .5a.5.5 0 0 0-1 0v6a.5.5 0 0 0 1 0V6Z" />
                                    <path
                                        d="M14.5 3a1 1 0 0 1-1 1H13v9a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V4h-.5a1 1 0 0 1-1-1V2a1 1 0 0 1 1-1H6a1 1 0 0 1 1-1h2a1 1 0 0 1 1 1h3.5a1 1 0 0 1 1 1v1ZM4.118 4 4 4.059V13a1 1 0 0 0 1 1h6a1 1 0 0 0 1-1V4.059L11.882 4H4.118ZM2.5 3h11V2h-11v1Z" />
                                </svg>
                            </div>
                        </td>
                    </tr>
                    `);
                });
                if(itemList.length){
                    tFoot.hide();
                }else{
                    tFoot.show();
                }
            }else{
                tbodyElm.empty();
                tFoot.show();
                if(!xhr.responseText){
                    console.log("couldn't connect with the server");
                    showToast('error','Failed','connection failed');
                }else{
                    console.log((JSON.parse(xhr.responseText)));
                    showToast('error','Failed','Failed to fetch items');
                }
            }
        }
    });

    xhr.addEventListener('loadstart',()=>{
        tFoot.text("Please wait");
    });

    xhr.addEventListener('loadend', ()=>{
        tFoot.text("No items records are found");
    });

    xhr.open('GET', 'http://localhost:8080/pos/items'+query ,true);
    xhr.send();
}
function formatItemCode(code){
    return `I${code.toString().padStart(3,'0')}`;
}
function validateData(){
    const price = txtPrice.val().trim();
    const quantity = txtQuantity.val().trim();
    const description = txtDesc.val().trim();
    let valid = true;
    resetForm();

    if (!price) {
        valid = invalidate(txtPrice, "price can't be empty");
    } else if (!/\d+/.test(price)) {
        valid = invalidate(txtPrice, 'Invalid price');
    }

    if (!quantity) {
        valid = invalidate(txtQuantity, "quantity number can't be empty");
    } else if (!/^[0-9]+$/.test(quantity)) {
        valid = invalidate(txtQuantity, 'Invalid quantity number');
    }

    if (!description) {
        valid = invalidate(txtDesc, "description can't be empty");
    } else if (!/^[A-Za-z ]+$/.test(description)) {
        valid = invalidate(txtDesc, "Invalid description");
    }

    return valid;
}

function invalidate(txt, msg) {
    setTimeout(() => txt.addClass('is-invalid animate__shakeX'), 0);
    txt.trigger('select');
    txt.next().text(msg);
    return false;
}
function resetForm(clearData) {
    [txtCode, txtDesc, txtPrice, txtQuantity].forEach(txt => {
        txt.removeClass("is-invalid animate__shakeX");
        if (clearData) txt.val('');
    });
}

getItems();
