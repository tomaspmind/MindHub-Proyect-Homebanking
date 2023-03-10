const { createApp } = Vue
    
createApp({

    data() {
        return {
        tipoDeLoan: [],
        loanId: '',
        loanPayments: undefined,
        loanAmount: '',
        loanAccount: '',
        accounts: [],
        loans: [],
        payments: [],
        fee: []
        
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
            this.guardFee = this.tipoDeLoan[0].fee
            this.guardFeePayments = this.tipoDeLoan[0].feePayments
            this.indicePayment = this.payments.indexOf(this.loanPayments)
            this.extra = this.guardFeePayments[this.indicePayment]
            console.log(this.extra);
            
        },
        paymentsForMonth(){
            let amountWithpercentage = this.loanAmount * this.guardFee
            let monthPayment = (amountWithpercentage / this.loanPayments * this.extra).toLocaleString('de-DE', { style: 'currency', currency: 'USD' })
            return monthPayment
        },
        pertentageFee(data){
            let pertentage = (data - 1)*100
            return Math.round(pertentage)
        },
        pertentageFeePayments(data){
            let porcentaje = (this.guardFeePayments[this.payments.indexOf(data)] - 1) * 100 
            return Math.round(porcentaje);
        },
        


    },
    computed: {
    
    },

}).mount('#app')
