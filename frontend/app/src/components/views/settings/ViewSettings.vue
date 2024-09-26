<template>
	<div class="w-full p-2">
		<Card style="margin-top: -10px !important">
			<template #title>{{ t("general.settings") }}</template>
			<template #content>
				<SettingsItem>
					<SettingsCourts />
				</SettingsItem>
			</template>
		</Card>
	</div>
</template>

<script setup lang="ts">
import { useI18n } from "vue-i18n"
import SettingsCourts from "@/components/views/settings/SettingsCourts.vue"
import SettingsItem from "@/components/views/settings/SettingsItem.vue"

const { t } = useI18n({ inheritLocale: true })
</script>

<style scoped></style>
