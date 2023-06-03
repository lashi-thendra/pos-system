const tbodyElm = $('#tbl-customers tbody');

tbodyElm.empty();


function getCustomers(){
    const tFoot = $('#tbl-customers tfoot tr td:first-child');
    const xhr = new XMLHttpRequest();

    xhr.addEventListener('readystatechange',()=>{
        if(xhr.readyState === 4){
            if(xhr.status === 200){
                console.log(xhr.responseText);
            }else{
                console.log("failed to fetch customers");
            }
        }
    });

    xhr.addEventListener('loadstart',()=>{
        tFoot.text("Please wait");
    });

    xhr.addEventListener('loadend', ()=>{
        tFoot.text("No customer records are found");
    });

    xhr.open('GET', 'http://localhost:8080/pos/customers' ,true);
    xhr.send();
}

