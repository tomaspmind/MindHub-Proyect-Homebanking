const {createApp} = Vue

createApp({
    data(){
        return{
            client: [],
            cardType: "",
            colorType: "",
            createdCardError:"",
            dataError: ""
    }
},
created(){
    this.loadData()
},
    methods: {
        loadData(){
            axios.get("http://localhost:8080/api/clients/current")
            .then(response =>{
                this.client = response.data
                
            })
            .catch(error => error.message)
        },
        createCard() {
            axios.post('/api/clients/current/cards', `type=${this.cardType}&color=${this.colorType}`, {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
            })
            .then(response => {
                Swal.fire({
                    title: `Acepted (Codigo: 200)`,
                    icon: "success",
                    showConfirmButton: false,
                    timer: 2000,
                    timerProgressBar: true,
                    }).then(response => {location.href = '/web/cards.html';})
            })
            .catch(error => {
                this.dataError = error.response.status
                this.createdCardError = error.response.data
                this.alertCreatCards()
            })},
        alertCreatCards(){
            swal({
            title: `${this.createdCardError}`+" "+`(error: ${this.dataError})`,
            text: "",
            icon: "error",
            dangerMode: true,
            })
        },
    }
}).mount('#app')
