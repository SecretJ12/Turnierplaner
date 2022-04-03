import { createApp } from 'vue'
import App from './App.vue'

/* import the fontawesome core */
import { library } from '@fortawesome/fontawesome-svg-core'

/* import specific icons */
import { faFlag, faRightToBracket } from '@fortawesome/free-solid-svg-icons'
import {  } from '@fortawesome/free-regular-svg-icons'

/* add icons to the library */
library.add(faFlag, faRightToBracket)

/* import font awesome icon component */
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'

/* add font awesome icon component */
createApp(App)
    .component('font-awesome-icon', FontAwesomeIcon)
    .mount('#app')
