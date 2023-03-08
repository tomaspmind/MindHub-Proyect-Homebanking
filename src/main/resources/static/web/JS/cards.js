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
                currentDay: undefined,
                number: ""
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
            },
            deleteCard(){
                axios.patch("/api/clients/current/cards", `number=${this.number}`)
                .then(response => {
                    Swal.fire({
                        title: `${response.data}`,
                        icon: "success",
                        showConfirmButton: false,
                        timer: 2000,
                        timerProgressBar: true,
                        }).then(response => {
                            location.href = '/web/cards.html';
                            this.loadData()
                        })
                })
            },
            alertDeleteCard(){
                Swal.fire({
                    title: 'Are you sure you want to delete this card?',
                    text: "You won't be able to revert this!",
                    icon: 'warning',
                    showCancelButton: true,
                    confirmButtonColor: '#3085d6',
                    cancelButtonColor: '#d33',
                    confirmButtonText: 'Confirm'
                }).then((result) => {
                    if (result.isConfirmed) {
                        this.deleteCard();
                }
                })
            },
            
        },
    
    }).mount('#app')
    