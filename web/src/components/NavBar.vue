<template>
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
  <div class="container">
    <router-link class="navbar-brand" :to="{name: 'home'}">King of Bots</router-link>
    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarText" aria-controls="navbarText" aria-expanded="false" aria-label="Toggle navigation">
      <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navbarText">
      <ul class="navbar-nav me-auto mb-2 mb-lg-0">
        <li class="nav-item">
          <router-link :class="route_name == 'pk_index' ? 'nav-link active' : 'nav-link'" 
          :to="{name: 'pk_index'}">Fighting</router-link>
        </li>
        <li class="nav-item">
          <RouterLink :class="route_name == 'record_index' ? 'nav-link active' : 'nav-link'" 
          :to="{name: 'record_index'}">Record</RouterLink>
        </li>
        <li class="nav-item">
          <RouterLink :class="route_name == 'ranklist_index' ? 'nav-link active' : 'nav-link'" 
          :to="{name: 'ranklist_index'}">Rank</RouterLink>
        </li>
      </ul>
      <span class="navbar-text me-auto">
        欢迎来到凌骎的小游戏
      </span> 
      <ul class="navbar-nav">
        <li class="nav-item dropdown">
          <a class="nav-link dropdown-toggle" href="#" id="navbarScrollingDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
            Name: wdb
          </a>
          <ul class="dropdown-menu" aria-labelledby="navbarScrollingDropdown">
            <li>
                <router-link class="dropdown-item" :to="{name: 'user_bot_index'}">我的</router-link>
            </li>
            <li><hr class="dropdown-divider"></li>
            <li><a class="dropdown-item" href="#">Exit</a></li>
          </ul>
        </li>
      </ul>
    </div>
  </div>
</nav>
</template>

<script>
import { useRoute } from 'vue-router';
import { computed } from 'vue'

export default {
    setup() {
        const route = useRoute();
        let route_name = computed(() => route.name) 
        return {
            route_name
        }
    }
}
</script>

<style scoped>

</style>


