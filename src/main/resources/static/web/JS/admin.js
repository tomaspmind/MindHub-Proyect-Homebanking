const { createApp } = Vue

createApp({

    data() {
        return {
            LoanName: "",
            LoanMaxAmount: undefined,
            LoanPayments: [],
            LoanFee: undefined,
            LoanFeePayments:[],
            nuevoLoanPayments:[],
            nuevoLoanFeePayments:[],
            previo: [],
            allLoans: [],
            formOpen: false,
            productData: {
            title: '',
            rating: '',
            price: '',
            list_price: '',
            is_featured: false
    }
        }
    },

    created(){
        this.loans()
        
    },

    methods: {
        resetForm: function () {
          this.productData = {
            title: '',
            rating: '',
            price: '',
            list_price: '',
            is_featured: false
          }
        },
        cancel: function() {
          this.formOpen = false;
          this.resetForm();
        },
        createLoan() {
            axios.post("/api/admin/loans", { name:this.LoanName, maxAmount:this.LoanMaxAmount, payment:this.previo, fee:this.LoanFee, feePayments:this.nuevoLoanFeePayments}, {
                headers: {
                    'Content-Type': 'application/json'
                }
            })
            .then(response => {
                location.href = '/web/admin.html';
            })
            .catch(error => {
                console.log(error+" error");
                console.log(this.LoanPayments.split(","));
                console.log(typeof(this.LoanFeePayments));
            })
        },
        changed(){
            
            this.nuevoLoanFeePayments = this.LoanFeePayments.split(",")
            console.log(this.nuevoLoanFeePayments);
        },
        changed2(){
            this.previo = this.LoanPayments.split(",")
            
            this.nuevoLoanPayments = this.previo.map(elemento => parseInt(elemento));
            
        },
        alertCreateLoan(){
            Swal.fire({
                title: 'Are you sure you want to create this Loan?',
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
        loans(){
            axios.get("/api/loans")
            .then(response =>{
                this.allLoans = response.data
            })
        },
        logout(){
            axios.post('/api/logout').then(response =>  Swal.fire({
            title: `Have a good day!`,
            icon: "success",
            showConfirmButton: false,
            timer: 1000,
            timerProgressBar: true,
            }).then(response => {location.href = '/web/index.html';}))
            
        },
        alertLogout(){
            Swal.fire({
            title: 'Are you sure you want to Log Out?',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Yes, Log out me'
            }).then((result) => {
            if (result.isConfirmed) {
                this.logout();
            }
            })
        },

    },
    mounted(){
        
    }

}).mount('#app')
