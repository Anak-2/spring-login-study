$('.logout-btn').on('click',function(){
    const accessToken = localStorage.getItem('accessToken');
    if(accessToken !== undefined || accessToken !== ''){
        localStorage.removeItem('accessToken');
        localStorage.removeItem('accessTokenExpirationTime');
        alert("로그아웃이 완료됐습니다");
        location.href="http://localhost:8080/jwt-login/home";
    }
    $.ajax({
        url:"http://localhost:8080/jwt-login/logout",
        type:"DELETE",
        success:function(result){

        },
        error:function(){

        }
    });
})