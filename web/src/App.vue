<template>
  <nav-bar/>

  <router-view/>
</template>

<script>
import NavBar from './components/NavBar.vue'
import "bootstrap/dist/css/bootstrap.min.css"
import "bootstrap/dist/js/bootstrap"
import $ from 'jquery'

export default {
  components: {
    NavBar
  },
  setup() {
    $.ajax({
      url: "http://127.0.0.1:3000/user/account/token/",
      type:"post",
      data: {
        username: 'admin',
        password: "123",
      },
      success(resp) {
        console.log(resp);
      },
      error(resp) {
        console.log(resp);
      }
    });

    $.ajax ({
      url: "http://127.0.0.1:3000/user/account/info/",
      type: "get", 
      headers: {
        Authorization: "Bearer " + "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI3NjAwNTBhOWUwNDI0MjBlYTZiNmMzMDU4MGFlNmY5NiIsInN1YiI6IjEiLCJpc3MiOiJzZyIsImlhdCI6MTY3OTMxNDY0NiwiZXhwIjoxNjgwNTI0MjQ2fQ.6thqU1GGhBh56H-FbC2IqX9QnHUpo7bwWkZFqycUc_g"
      },
      success(resp) {
        console.log(resp);
      },
      error(resp) {
        console.log(resp);
      }
    });

    $.ajax ({
      url: "http://127.0.0.1:3000/user/account/register/",
      type: "post", 
      data: {
        username: "a",
        password: "123456",
        confirmedPassword: "123456",
      },
      success(resp) {
        console.log(resp);
      },
      error(resp) {
        console.log(resp);
      }
    });
  }
}
</script>

<style>
body {
  background-image: url("./assets/images/background.jpeg");
  background-size: cover;
} 
</style>