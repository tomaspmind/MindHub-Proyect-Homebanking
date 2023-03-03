$(document).ready(function () {
    $("#sidebarCollapse").on("click", function () {
        $("#sidebar").toggleClass("active");
        $(this).toggleClass("active");
    });
    });
    
    
    
    const { createApp } = Vue
    
    createApp({
    
        data() {
            return {
                client: [],
                dataCards: []
            }
        },
    
        created(){
            this.loadData()
        },
    
        methods:{
            loadData(){
                axios.get("http://localhost:8080/api/clients/current")
                .then(response =>{
                    this.client = response.data
                    this.dataCards = this.client.cards
                    
    
                })
                .catch(error => error.message)
            },

            justDay(data){
                let day = data.split("-")[1]
                let year = data.split("-")[0]
                return year.slice(2,4)+"/"+day
            },
            card(){
                VanillaTilt.init(document.querySelector(".front"), {
                    reverse: true,
                    glare: true,
                    max: 15,
                    speed: 3000
                });
                
            },
            logout(){
                axios.post('/api/logout').then(response => window.location.href = '/web/index.html')
            },
        },
    
    }).mount('#app')
    