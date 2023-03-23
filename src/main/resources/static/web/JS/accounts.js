$(document).ready(function () {
$("#sidebarCollapse").on("click", function () {
    $("#sidebar").toggleClass("active");
    $(this).toggleClass("active");
});
});

var $keys = $(".calculator button");
var $screen = $(".screen");
var decimal = false;
var operators = ["+", "-", "x", "÷"];

$keys.click(function () {
  var keyVal = $(this).data("val");
  output = $(".screen").html();

  console.log(keyVal);

  // clear
  if (keyVal == "clear") {
    $screen.html("");
    decimal = false;
  }
  // equal
  else if (keyVal == "=") {
    var equation = output;
    var lastChar = equation[equation.length - 1];
    equation = equation.replace(/x/g, "*").replace(/÷/g, "/");
    if (operators.indexOf(lastChar) > -1 || lastChar == ".")
      equation = equation.replace(/.$/, "");
    if (equation) {
      $screen.html(eval(equation));
    }
    decimal = false;
  }
  // operators
  else if ($(this).parent().is(".operators")) {
    var lastChar = output[output.length - 1];
    if (output != "" && operators.indexOf(lastChar) == -1) {
      $screen.html($screen.html() + keyVal);
    } else if (output == "" && keyVal == "-") {
      $screen.html($screen.html() + keyVal);
    }
    if (operators.indexOf(lastChar) > -1 && output.length > 1) {
      $screen.html($screen.html().replace(/.$/, keyVal));
    }
    decimal = false;
  }
  // decimal
  else if (keyVal == ".") {
    if (!decimal) {
      $screen.html($screen.html() + keyVal);
      decimal = true;
    }
  }
  // buttons
  else {
    $screen.html($screen.html() + keyVal);
  }
});

$(window)
  .keydown(function (e) {
    console.log(e.which);
    switch (e.which) {
      case 96:
        key = 0;
        break;
      case 97:
        key = 1;
        break;
      case 98:
        key = 2;
        break;
      case 99:
        key = 3;
        break;
      case 100:
        key = 4;
        break;
      case 101:
        key = 5;
        break;
      case 102:
        key = 6;
        break;
      case 103:
        key = 7;
        break;
      case 104:
        key = 8;
        break;
      case 105:
        key = 9;
        break;
      case 111:
        key = "÷";
        break;
      case 109:
        key = "-";
        break;
      case 106:
        key = "x";
        break;
      case 107:
        key = "+";
        break;
      case 13:
        key = "=";
        break;
      case 110:
        key = ".";
        break;
      case 27:
        key = "clear";
        break;
      default:
        return false;
    }

    $('[data-val="' + key + '"]')
      .addClass("active")
      .click();
  })
  .keyup(function () {
    $(".active").removeClass("active");
  });

/*--------------------
Codepen Preview Tile
--------------------*/
$('[data-val="clear"]')
  .click()
  .delay(100)
  

  /* ------------------- */

  $(function () {
    function c() {
      p();
      var e = h();
      var r = 0;
      var u = false;
      l.empty();
      while (!u) {
        if (s[r] == e[0].weekday) {
          u = true;
        } else {
          l.append('<div class="blank"></div>');
          r++;
        }
      }
      for (var c = 0; c < 42 - r; c++) {
        if (c >= e.length) {
          l.append('<div class="blank"></div>');
        } else {
          var v = e[c].day;
          var m = g(new Date(t, n - 1, v)) ? '<div class="today">' : "<div>";
          l.append(m + "" + v + "</div>");
        }
      }
      var y = o[n - 1];
      a.css("background-color", y)
        .find("h1")
        .text(i[n - 1] + " " + t);
      f.find("div").css("color", y);
      l.find(".today").css("background-color", y);
      d();
    }
    function h() {
      var e = [];
      for (var r = 1; r < v(t, n) + 1; r++) {
        e.push({ day: r, weekday: s[m(t, n, r)] });
      }
      return e;
    }
    function p() {
      f.empty();
      for (var e = 0; e < 7; e++) {
        f.append("<div>" + s[e].substring(0, 3) + "</div>");
      }
    }
    function d() {
      var t;
      var n = $("#calendar").css("width", e + "px");
      n.find((t = "#calendar_weekdays, #calendar_content"))
        .css("width", e + "px")
        .find("div")
        .css({
          width: e / 7 + "px",
          height: e / 7 + "px",
          "line-height": e / 7 + "px"
        });
      n.find("#calendar_header")
        .css({ height: e * (1 / 7) + "px" })
        .find('i[class^="icon-chevron"]')
        .css("line-height", e * (1 / 7) + "px");
    }
    function v(e, t) {
      return new Date(e, t, 0).getDate();
    }
    function m(e, t, n) {
      return new Date(e, t - 1, n).getDay();
    }
    function g(e) {
      return y(new Date()) == y(e);
    }
    function y(e) {
      return e.getFullYear() + "/" + (e.getMonth() + 1) + "/" + e.getDate();
    }
    function b() {
      var e = new Date();
      t = e.getFullYear();
      n = e.getMonth() + 1;
    }
    var e = 480;
    var t = 2013;
    var n = 9;
    var r = [];
    var i = [
      "JANUARY",
      "FEBRUARY",
      "MARCH",
      "APRIL",
      "MAY",
      "JUNE",
      "JULY",
      "AUGUST",
      "SEPTEMBER",
      "OCTOBER",
      "NOVEMBER",
      "DECEMBER"
    ];
    var s = [
      "Sunday",
      "Monday",
      "Tuesday",
      "Wednesday",
      "Thursday",
      "Friday",
      "Saturday"
    ];
    var o = [
      "#BB1C0E"
    ];
    var u = $("#calendar");
    var a = u.find("#calendar_header");
    var f = u.find("#calendar_weekdays");
    var l = u.find("#calendar_content");
    b();
    c();
    a.find('i[class^="icon-chevron"]').on("click", function () {
      var e = $(this);
      var r = function (e) {
        n = e == "next" ? n + 1 : n - 1;
        if (n < 1) {
          n = 12;
          t--;
        } else if (n > 12) {
          n = 1;
          t++;
        }
        c();
      };
      if (e.attr("class").indexOf("left") != -1) {
        r("previous");
      } else {
        r("next");
      }
    });
  });


const { createApp } = Vue

createApp({

    data() {
        return {
            client: [],
            accounts: [],
            all_transactions: [],
            dataLoans: [],
            dataCards: [],
            accountNumber: "",
            accountDestiny: "",
            accountType: "",
            newList: []
        }
    },

    created(){
        this.loadData(),
        this.totalBalance()
    },

    methods:{
        
        loadData(){
            axios.get("http://localhost:8080/api/clients/current")
            .then(response =>{
                this.client = response.data
                this.accounts = this.client.accounts
                this.dataLoans = this.client.loans
                this.allTransactions()
                this.all_transactions = this.all_transactions.sort((a,b) => new Date(b.date).getTime() - new Date(a.date).getTime())
                this.dataCards = this.client.cards
            })
            .catch(error => error.message)
        },

        parseDate(date){
            return date.split("T")[0]
        },

        totalBalance() {
        let suma = 0;
        for (let account of this.accounts) {
            suma += account.balance;
        }
        return suma.toLocaleString('de-DE', { style: 'currency', currency: 'USD' });
        },

        allTransactions(){
            for(account of this.accounts){
                for (transaction of account.transaction) {
                    this.all_transactions.push(transaction)
                }
            }
        },

        justHour(data){
            let hour = data.split("T")[1]
            return hour.substr(0,8)
        },

        logout(){
            axios.post('/api/logout').then(response =>  Swal.fire({
              title: `Have a good day ${this.client.firstName}`,
              icon: "success",
              showConfirmButton: false,
              timer: 1000,
              timerProgressBar: true,
              }).then(response => {location.href = '/web/index.html';}))
            
        },

        CreateNewAccount(){
            axios.post('/api/clients/current/accounts', `accountType=${this.accountType}`)
            .then(response => {
                this.loadData();
                location.href = "/web/accounts.html"
            })
            .catch(error => {
                this.error = error.response.data.message;
            });
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
        alertDeleteAccount(){
          Swal.fire({
            title: 'Are you sure you want to Delete this account?',
            text: "You won't be able to revert this!",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Yes, Delete'
          }).then((result) => {
            if (result.isConfirmed) {
                this.deleteAccount();
            }
          })
        },
        deleteAccount(){
          axios.patch("/api/clients/current/accounts", `accountNumber=${this.accountNumber}&accountDestiny=${this.accountDestiny}`)
          .then(response => {
            Swal.fire({
                title: `${response.data}`,
                icon: "success",
                showConfirmButton: false,
                timer: 2000,
                timerProgressBar: true,
                }).then(response => {
                    location.href = '/web/accounts.html';
                    this.loadData()
                })
        })
        },
        alertCreateAccount(){
          Swal.fire({
            title: 'Are you sure you want to create a new Account?',
            text: "You won't be able to revert this!",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Yes, please'
          }).then((result) => {
            if (result.isConfirmed) {
                this.CreateNewAccount();
            }
          })
        },
        changed(){
          this.newList = this.accounts.filter(element => element.number !== this.accountNumber)
          return this.newList
      }
    },

}).mount('#app')
