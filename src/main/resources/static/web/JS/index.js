const {createApp} = Vue

createApp({
    data(){
        return{
        email: '',
        password: '',
        error: [],
        firstName: '',
        lastName: '',
        registerError:"",
        dataError: "",
        onlyName: ""
    }
},
created(){
    this.formulario()
},
    methods: {
        login() {
            axios.post('/api/login', `email=${this.email}&password=${this.password}`, {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
            })
            .then(response => {
                this.onlyName = this.email.split("@")[0]
                Swal.fire({
                    title: `Welcome ${this.onlyName} !`,
                    icon: "success",
                    showConfirmButton: false,
                    timer: 1500,
                    timerProgressBar: true,
                    }).then(() => {
                        if(this.email == "admin@mindhub.com"){
                            location.href = "/web/admin.html";
                        }else{
                            location.href = '/web/accounts.html';
                        }
                    })
            })
            .catch(error => {
                this.error = error.response.data;
                this.alertLogin()
                console.log(this.error);
            });
        },
        register() {
            if(/^[a-zA-Z0-9.!#$%&'+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:.[a-zA-Z0-9-]+)$/.test(this.email)){
                axios.post('/api/clients', `first=${this.firstName}&lastName=${this.lastName}&email=${this.email}&password=${this.password}`, {
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded'
                    }
                })
                .then(response => {
                    this.login()
                })
                .catch(error => {
                    this.dataError = error.response.status
                    this.registerError = error.response.data
                    this.alertIndex()
                });
            }else{
                this.alertFailEmail()
            }
            
        },
        formulario(){
            $(document).ready(function() {
                $(".login-form").hide();
                $(".login").css("background", "none");
                $(".login").click(function() {
                $(".signup-form").hide();
                $(".login-form").show();
                $(".signup").css("background", "none")
                $(".login").css("background", "#6D0B0B");
                });
                $(".signup").click(function() {
                $(".login-form").hide();
                $(".signup-form").show();
                $(".signup").css("background", "#6D0B0B")
                $(".login").css("background", "none");
                });
                $(".btn").click(function() {
                $(".input").val("");
                });
            });
        },
        alertIndex(){
            Swal.fire({
            title: `${this.registerError}`+" "+`(error: ${this.dataError})`,
            text: "",
            icon: "error",
            dangerMode: true,
            })
        },
        alertLogin(){
            Swal.fire({
            title: `Bad Request`,
            text: "",
            icon: "error",
            dangerMode: true,
            })
        },
        alertFailEmail(){
            Swal.fire({
            title: `Missing data`,
            text: "",
            icon: "error",
            dangerMode: true,
            })
        },
        
    }
}).mount('#app')
