const { createApp } = Vue

createApp({

    data() {
        return {
            data: undefined,
            listClients: [],
            extraClient: {firstName: "", lastName: "", email: "", id: ""},
            newExtraClient: {firstName: "", lastName: "", email: "", id: ""},
            beingEdited: false,
            
        }
    },

    created(){
        this.loadData()
    },

    methods:{

        loadData(){
            axios.get("http://localhost:8080/api/clients")
            .then(response =>{
                this.data = response
                this.listClients = response
                
            })
            .catch(error => error.message)
        },

        addClient(){
                axios.post("http://localhost:8080/api/clients", this.extraClient)
                .then (response =>{
                    this.loadData()
                    this.cleanForm()
            })
    },

        deleteClient(client){
            axios.delete(client.id)
            .then (response => {
                this.loadData()
            })
        },

        editClient(){
            this.extraClient = this.newExtraClient
            axios.put(this.extraClient.id, this.extraClient)
            .then(response => {
                this.loadData()
                this.beingEdited = false;
                this.cleanForm()
            })
        },

        editing(client){
            this.extraClient.firstName = client.firstName;
            this.extraClient.lastName = client.lastName;
            this.extraClient.email = client.email;
            this.extraClient.id = client.id;
            this.beingEdited = true;
        },

        cleanForm(){
            this.extraClient = {firstName: "", lastName: "", email: ""};
        }
    }

}).mount('#app')