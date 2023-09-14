<template>
  <NavBar />
  <router-view/>
</template>

<script>
import NavBar from './components/NavBar.vue'
import "bootstrap/dist/css/bootstrap.min.css"
import "bootstrap/dist/js/bootstrap"
// import $ from 'jquery'
export default {
  components: {
    NavBar
  },
  setup(){
    

    // $.ajax({
    //   url: "http://127.0.0.1:3000/user/account/info/",
    //   type: "get",
    //   headers: {
    //     Authorization: "Bearer " + "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJiMzFkNjI5MjEwNmM0YzA3OTkzMzk4M2I3ZGM5MzQyMCIsInN1YiI6IjEiLCJpc3MiOiJzZyIsImlhdCI6MTY5MzgxODIwMCwiZXhwIjoxNjk1MDI3ODAwfQ.ANyKznJaP-d3yEl2sT4wpEgQty546b4WKR_S3bKSdig"
    //   },
    //   success(resp) {
    //     console.log(resp);
    //   },
    //   error(resp) {
    //     console.log(resp);
    //   }
    // });

    // $.ajax({
    //   url: "http://127.0.0.1:3000/user/account/register/",
    //   type: "post",
    //   data: {
    //     username: "lcc",
    //     password: "plcjc",
    //     confirmedPassword: "plcc",
    //   },
    //   success(resp) {
    //     console.log(resp);
    //   },
    //   error(resp) {
    //     console.log(resp);
    //   }

    // });

  }
}

</script>

<style>
body {
  background-image: url("@/assets/images/background.png");
  background-size: cover;
}
</style>
