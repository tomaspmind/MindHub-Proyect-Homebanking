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
                dataCards: [],
                transactionId: [],
                currentDay: undefined
            }
        },
    
        created(){
            this.loadData()
            this.actualDay()
        
        },
    
        methods:{
            loadData(){
                axios.get("http://localhost:8080/api/clients/current")
                .then(response =>{
                    this.client = response.data
                    this.dataCards = this.client.cards
                    this.transactionId = this.client.accounts[0].id
                    
                    
                    console.log(this.currentDay);
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
            actualDay(){
                let fecha = new Date();
                let opciones = { year: 'numeric', month: '2-digit', day: '2-digit' };
                let fechaFormateada = fecha.toLocaleDateString('es-ES', opciones);
                let onlyDay = fechaFormateada.split("/")[0]
                let onlyMonth = fechaFormateada.split("/")[1]
                let onlyYear = fechaFormateada.split("/")[2]
                return this.currentDay = onlyYear+"-"+onlyMonth+"-"+onlyDay
            }
            
        },
    
    }).mount('#app')
    