import { resolve } from "path"
import { defineConfig } from "vite"
import vue from "@vitejs/plugin-vue"
import Components from "unplugin-vue-components/vite"
import { PrimeVueResolver } from "unplugin-vue-components/resolvers"

// https://vitejs.dev/config/
export default defineConfig({
	server: {
		host: true,
		port: 3000,
	},
	plugins: [
		vue(),
		Components({
			resolvers: [PrimeVueResolver()],
		}),
	],
	resolve: {
		alias: {
			"@": resolve(__dirname, "src"),
		},
		extensions: [".js", ".ts"],
	},
	define: {
		__VUE_I18N_FULL_INSTALL__: true,
		__VUE_I18N_LEGACY_API__: false,
		__INTLIFY_PROD_DEVTOOLS__: false,
	},
})
