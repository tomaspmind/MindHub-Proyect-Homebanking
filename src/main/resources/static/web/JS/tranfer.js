const { createApp } = Vue
    
createApp({

    data() {
        return {
        tipoTransferencia: '',
        cuentaDestino: '',
        cuentaDestino2: '',
        amount: '',
        description: '',
        accounts: []
        }
    },

    created(){
        this.loadAccounts()
    },

    methods: {
        loadAccounts() {
            axios.get("http://localhost:8080/api/clients/current/accounts")
            .then(response => {
                this.accounts = response.data
            })
        },
        createTranfer() {
            axios.post('/api/transactions', `amount=${this.amount}&description=${this.description}&accountNumber=${this.cuentaDestino}&accountNumberDestini=${this.cuentaDestino2}`, {
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
                    }).then(response => {location.href = '/web/transfers.html';})
            })
            .catch(error => {
                Swal.fire({
                    icon: 'error',
                    title: `${error.response.data}`,
                    text: 'Something went wrong!',
                })
            })
        },
        alertTranfer(){
            Swal.fire({
                title: 'Are you sure you want to Tranfer?',
                text: "You won't be able to revert this!",
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#3085d6',
                cancelButtonColor: '#d33',
                confirmButtonText: 'Confirm'
            }).then((result) => {
                if (result.isConfirmed) {
                    this.createTranfer();
            }
            })
        },
        cleanForm(){
            this.cuentaDestino = ''
            this.cuentaDestino2 = ''
            this.tipoTransferencia = ''
            this.amount = ''
            this.description = ''
        }

    },
    computed: {
    
    },

}).mount('#app')
