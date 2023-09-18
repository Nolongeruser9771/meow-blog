//Truy cập vào các nút có chức năng xử lí ảnh

const modalImageBtn = document.getElementById('btn-modal-image');
const chooseImageBtn = document.getElementById('btn-chose-image');
const uploadImageBtn = document.getElementById('avatar');
const deleteImageBtn = document.getElementById('btn-delete-image');
const imageContainer = document.querySelector('.image-container');
const imageThumbnailEl = document.getElementById('thumbnail');

let imageList = [];

modalImageBtn.addEventListener('click', async () => {
    chooseImageBtn.disabled = true;
    deleteImageBtn.disabled = true;
    try {
        //user id fixed to 1
        //Lấy danh sách hình ảnh json
        const imageFetch = await fetch('/api/v1/users/1/files');
        const data = await imageFetch.json();
        console.log(data);
        imageList = data;

        //Xử lí hiển thị
        renderPagination(imageList);
    } catch (e) {
        console.log(e);
    }
})

function renderImages(imageList){
    //xóa nội dung đang có trong image-container
    imageContainer.innerHTML ="";

    //Tạo nội dung (bản chất là cộng chuỗi)
    let htmlEl = "";
    imageList.forEach(image => {
        htmlEl+= `
        <div class="image-item" onclick="chooseImage(this)" data-id="${image.id}">
             <img src="/api/v1/files/${image.id}" alt="">
        </div>`
    })
    console.log(htmlEl)

    //Insert nội dung
    imageContainer.innerHTML = htmlEl;
}

//Hiển thị phân trang
function renderPagination(imageList){
    $('.pagination-container').pagination({
        dataSource: imageList,
        pageSize: 8,
        callback: function(data, pagination) {
            //hiển thị image dựa trên data trả về
            renderImages(data);
        }
    })};

//Chọn ảnh
    //Tại 1 Thời điểm chỉ được chọn 1 ảnh
    //Ảnh được chọn sẽ highlight lên
    //Nếu ko có hình ảnh được chọn, disable nút
function chooseImage(el) {
    //Xóa class selected của phần tử được chọn trước
    const selectedImage = document.querySelector(".image-item.selected");
    if (selectedImage) {
        selectedImage.classList.remove('selected')
    }

    //add class seleted vào class mới
    el.classList.add('selected');

    //Active 2 nút chức năng del và choose
    chooseImageBtn.disabled = false;
    deleteImageBtn.disabled = false;
}

//Hiển thị ảnh vào thumbnail
chooseImageBtn.addEventListener('click',() => {
    //Lấy ra hình ảnh đang chọn
    const selectedImage = document.querySelector(".image-item.selected");
    const apiUrl = selectedImage.querySelector('img').getAttribute('src');
    imageThumbnailEl.setAttribute('src', apiUrl);

    //Đóng modal
    $('#modal-xl').modal('hide');
})

//Xóa ảnh
deleteImageBtn.addEventListener('click', async () => {
    const isConfirm = window.confirm("Sure to delete this image?");
    if(!isConfirm){
        return;
    }
    try {
        //lấy ra hình ảnh được chọn
        const selectedImage = document.querySelector(".image-item.selected");

        //truy cập ảnh cần xóa
        const imageId = +selectedImage.dataset.id;
        console.log(imageId)

        //fetchAPI
        const deleteFetch = await fetch(`/api/v1/files/${imageId}`, {method: "DELETE"})
        //remove trên giao diện
        imageList = imageList.filter(image => image.id !== imageId);
        renderPagination(imageList);

        //deactive 2 nnút chức năng
        chooseImageBtn.disabled = true;
        deleteImageBtn.disabled = true;

        alert("Delete image successfully")
    } catch (e) {
        alert("Fail to delete image")
        console.log(e);
    }
})

//Upload file
uploadImageBtn.addEventListener('change',async (event) => {
    //Lấy file
    const file = event.target.files[0];
    console.log(file);

    //Tạo form-data
    const formData = new FormData();
    formData.append("file", file);

    //fetch
    try {
        const fetchResult = await fetch('/api/v1/files', {
            method: "POST",
            body: formData
        });
        const res = await fetchResult.json();
        console.log(res);

        //Lưu vào imageList (lưu lên đầu list)
        imageList.unshift(res);
        renderPagination(imageList);

    } catch (e) {
        console.log(e);
    }
})