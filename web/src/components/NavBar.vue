<template>
  <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container">
      <router-link class="navbar-brand" :to="{ name: 'home' }">King of Bots</router-link>
      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarText"
        aria-controls="navbarText" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div class="collapse navbar-collapse" id="navbarText">
        <ul class="navbar-nav me-auto mb-2 mb-lg-0">
          <li class="nav-item">
            <router-link :class="route_name == 'pk_index' ? 'nav-link active' : 'nav-link'"
              :to="{ name: 'pk_index' }">Fighting</router-link>
          </li>
          <li class="nav-item">
            <RouterLink :class="route_name == 'record_index' ? 'nav-link active' : 'nav-link'"
              :to="{ name: 'record_index' }">Record</RouterLink>
          </li>
          <li class="nav-item">
            <RouterLink :class="route_name == 'ranklist_index' ? 'nav-link active' : 'nav-link'"
              :to="{ name: 'ranklist_index' }">Rank</RouterLink>
          </li>
        </ul>
        <span class="navbar-text me-auto">
          欢迎来到凌骎的小游戏
        </span>
        <ul class="navbar-nav" v-if="$store.state.user.is_login">
          <li class="nav-item dropdown">
            <a class="nav-link dropdown-toggle" href="#" id="navbarScrollingDropdown" role="button"
              data-bs-toggle="dropdown" aria-expanded="false">
              {{ $store.state.user.username }}
            </a>
            <ul class="dropdown-menu" aria-labelledby="navbarScrollingDropdown" style="margin: 0;">
              <li>
                <router-link class="dropdown-item" :to="{ name: 'user_bot_index' }">我的</router-link>
              </li>
              <li>
                <hr class="dropdown-divider">
              </li>
              <li><a class="dropdown-item" href="#" @click="logout">Exit</a></li>
            </ul>
          </li>
        </ul>
        <ul class="navbar-nav" v-else-if="!$store.state.user.pulling_info">
          <li class="nav-item">
            <router-link class="nav-link" :to="{ name: 'user_account_login' }" role="button" aria-expanded="false">
              Sign In
            </router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link" :to="{ name: 'user_account_register' }" role="button" aria-expanded="false">
              Sign Up
            </router-link>
          </li>
        </ul>
      </div>
    </div>
  </nav>
</template>

<script>
import { useRoute } from 'vue-router';
import { computed } from 'vue'
import { useStore } from 'vuex';

export default {
  setup() {
    const store = useStore();
    const route = useRoute();
    let route_name = computed(() => route.name)

    const logout = () => {
      store.dispatch("logout");
    }

    return {
      route_name,
      logout
    }
  }
}
</script>

<style scoped></style>


