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
            account:[],
            objAccount: {},
            transactions: [],
            client: [],
            accounts: [],
            filtro: "",
            tipoOrden: "",
            filtro2: ""
        }
    },

    created(){
        this.loadData()
        this.clientData()
        
    },

    methods:{
        loadData(){
            let id = new URLSearchParams(location.search).get("id")
            axios.get(`http://localhost:8080/api/accounts/${id}`)
            .then(response =>{
                this.account = response
                this.objAccount = this.account.data
                
                this.transactions = this.objAccount.transaction.sort((a,b) => new Date(b.date).getTime() - new Date(a.date).getTime())

                console.log(this.transactions);
                
            })
            .catch(error => error.message)
        },
        parseDate(date){
            return date.split("T")[0]
        },
        justHour(data){
            let hour = data.split("T")[1]
            return hour.substr(0,8)
        },

        clientData(){
            axios.get("http://localhost:8080/api/clients/current")
            .then(response =>{
                this.client = response.data
                this.accounts = this.client.accounts
                
            })
            .catch(error => error.message)
        },
        logout(){
            axios.post('/api/logout').then(response => window.location.href = '/web/index.html')
        },
        refresh(){
            this.filtro = ""
            this.filtro2 = ""
        }
    },
    computed: {
        elementsFiltered() {
        return this.transactions.filter(transaction => {
            return transaction.description.toLowerCase().includes(this.filtro.toLowerCase());
        });
        },
        elementsFilteredForDate() {
            return this.transactions.filter(transaction => {
                return transaction.date.includes(this.filtro2);
            });
            },
    }

}).mount('#app')

