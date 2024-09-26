<template>
	<div class="w-full p-2 pt-0 grid">
		<SettingsItem v-if="isAdmin">
			<Card>
				<template #title>{{ t("general.courts") }}</template>
				<template #content>
					<SettingsGeneral />
				</template>
			</Card>
		</SettingsItem>
		<SettingsItem v-if="isDirector">
			<Card>
				<template #title>{{ t("general.courts") }}</template>
				<template #content>
					<SettingsCourts />
				</template>
			</Card>
		</SettingsItem>
	</div>
</template>

<script setup lang="ts">
import { useI18n } from "vue-i18n"
import SettingsCourts from "@/components/views/settings/SettingsCourts.vue"
import SettingsItem from "@/components/views/settings/SettingsItem.vue"
import SettingsGeneral from "@/components/views/settings/SettingsGeneral.vue"
import { getIsAdmin, getIsDirector } from "@/backend/security"
import { inject, ref } from "vue"

const { t } = useI18n()

const isLoggedIn = inject("loggedIn", ref(false))
const { data: isAdmin } = getIsAdmin(isLoggedIn)
const { data: isDirector } = getIsDirector(isLoggedIn)
</script>

<style scoped></style>
