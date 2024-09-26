<template>
	<div class="flex flex-column gap-2">
		<div class="flex flex-row">
			<InputGroup>
				<Dropdown v-model="loc" :options="availableLocales" />
				<Button @click="() => saveLanguage(loc)"
					>{{ t("settings.save_default") }}
				</Button>
			</InputGroup>
		</div>
		<div class="flex flex-row">
			<InputGroup>
				<InputText
					v-model="title"
					:disabled="!config"
					:placeholder="t('general.title')"
				/>
				<Button @click="() => saveTitle(title)"
					>{{ t("settings.save_default") }}
				</Button>
			</InputGroup>
		</div>
	</div>
</template>

<script setup lang="ts">
import { useI18n } from "vue-i18n"
import { inject, ref, watch } from "vue"
import { getConfig, useSaveDefault, useSaveTitle } from "@/backend/config"

const { t, locale, availableLocales } = useI18n()
const loc = ref(locale.value)
const isLoggedIn = inject("loggedIn", ref(false))

const { data: config } = getConfig(isLoggedIn)
const { mutate: saveLanguage } = useSaveDefault(isLoggedIn)
const { mutate: saveTitle } = useSaveTitle(isLoggedIn)
const title = ref("")
watch(
	config,
	() => {
		if (config.value) {
			if (config.value.title === "title") title.value = ""
			else title.value = config.value.title
		}
	},
	{ immediate: true },
)
</script>

<style scoped></style>
