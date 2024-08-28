import { resolve } from "path"
import { defineConfig } from "vite"
import vue from "@vitejs/plugin-vue"
import AutoImport from "unplugin-auto-import/vite"
import Components from "unplugin-vue-components/vite"

// https://vitejs.dev/config/
export default defineConfig({
	server: {
		host: true,
	},
	plugins: [
		vue(),
		AutoImport({
			resolvers: [],
		}),
		Components({
			resolvers: [],
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
