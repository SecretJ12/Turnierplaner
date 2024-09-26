<template>
	<div class="flex flex-column">
		<div class="flex flex-row">
			<InputGroup>
				<Dropdown v-model="loc" :options="availableLocales" />
				<Button @click="() => saveLanguage(loc)">{{
					t("settings.save_default")
				}}</Button>
			</InputGroup>
		</div>
	</div>
</template>

<script setup lang="ts">
import { useI18n } from "vue-i18n"
import { inject, ref } from "vue"
import { useSaveDefault } from "@/backend/config"

const { t, locale, availableLocales } = useI18n()
const loc = ref(locale.value)
const isLoggedIn = inject("loggedIn", ref(false))

const { mutate: saveLanguage } = useSaveDefault(isLoggedIn)
</script>

<style scoped></style>
