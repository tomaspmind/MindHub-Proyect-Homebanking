const { createApp } = Vue
    
createApp({

    data() {
        return {
        tipoDeLoan: {},
        loanId: '',
        loanPayments: '',
        loanAmount: '',
        loanAccount: '',
        accounts: [],
        loans: [],
        payments: []
        }
    },

    created(){
        this.loadAccounts()
        this.Loans()
    },

    methods: {
        loadAccounts() {
            axios.get("http://localhost:8080/api/clients/current/accounts")
            .then(response => {
                this.accounts = response.data
            })
        },
        Loans(){
            axios.get("http://localhost:8080/api/loans")
            .then(response =>{
                this.loans = response.data
                
            })
        },
        createLoan() {
            axios.post('/api/loans', { id:this.loanId, amount:this.loanAmount, payments:this.loanPayments, accountNumber:this.loanAccount }, {
                headers: {
                    'Content-Type': 'application/json'
                }
            })
            .then(response => {
                Swal.fire({
                    title: `${response.data}`,
                    icon: "success",
                    showConfirmButton: false,
                    timer: 2000,
                    timerProgressBar: true,
                    }).then(response => {location.href = '/web/accounts.html';})
            })
            .catch(error => {
                Swal.fire({
                    icon: 'error',
                    title: `${error.response.data}`,
                    text: 'Something went wrong!',
                })
            })
        },
        alertLoan(){
            Swal.fire({
                title: 'Are you sure you want to put a Loan??',
                text: "You won't be able to revert this!",
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#3085d6',
                cancelButtonColor: '#d33',
                confirmButtonText: 'Confirm'
            }).then((result) => {
                if (result.isConfirmed) {
                    this.createLoan();
            }
            })
        },
        cleanForm(){
            this.loanId = ''
            this.loanPayments = ''
            this.loanAmount = ''
            this.loanAccount = ''
        },
        changed(){
            this.tipoDeLoan = this.loans.filter(loan => loan.id == this.loanId)
            this.payments = this.tipoDeLoan[0].payment
            console.log(this.tipoDeLoan);
        },
        paymentsForMonth(){
            let amountWithpercentage = this.loanAmount * 0.2 + this.loanAmount
            let monthPayment = (amountWithpercentage / this.loanPayments).toLocaleString('de-DE', { style: 'currency', currency: 'USD' })
            return monthPayment
        }


    },
    computed: {
    
    },

}).mount('#app')
