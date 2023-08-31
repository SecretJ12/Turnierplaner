<template>
	<!--TODO add internalization-->
	<div class="flex justify-content-center w-full">
		<Card id="card">
			<template #title> Tournament registration</template>
			<template #content>
				<div class="formgrid grid">
					<div class="field col-10">
						<label for="name">{{ t("general.name") }}</label>
						<input
							id="name"
							type="text"
							v-bind="name"
							maxlength="30"
							class="text-base text-color surface-overlay p-2 border-1 border-solid surface-border border-round appearance-none outline-none focus:border-primary w-full"
						/>
						<small id="name-error" class="p-error">{{
							t(errors.name || "")
						}}</small>
					</div>
					<div class="field col flex flex-column col-2">
						<label for="visible" class="text-900">{{
							t("TournamentSettings.visible")
						}}</label>
						<div class="h-full flex align-items-center">
							<InputSwitch id="visible" v-bind="visible" />
						</div>
					</div>
					<div class="field col-12">
						<label for="description">{{ t("general.description") }}</label>
						<textarea
							id="description"
							type="text"
							rows="4"
							maxlength="255"
							class="text-base text-color surface-overlay p-2 border-1 border-solid surface-border border-round appearance-none outline-none focus:border-primary w-full"
							v-bind="description"
						></textarea>
					</div>
					<div class="field col-6">
						<label for="registration">{{
							t("TournamentSettings.registration_phase")
						}}</label>
						<Calendar
							id="registration"
							v-bind="registration_phase"
							selectionMode="range"
							:manualInput="false"
							class="w-full"
							:dateFormat="t('date_format')"
							showTime
							show-icon
						/>
						<small id="registration_phase-error" class="p-error">{{
							t(errors.registration_phase || "")
						}}</small>
					</div>
					<div class="field col-6">
						<label for="game_phase">{{
							t("TournamentSettings.game_phase")
						}}</label>
						<Calendar
							id="game_phase"
							v-bind="game_phase"
							selectionMode="range"
							:manualInput="false"
							class="w-full"
							:dateFormat="t('date_format')"
							showTime
							show-icon
						/>
						<small id="game_phase-error" class="p-error">{{
							t(errors.game_phase || "")
						}}</small>
					</div>
				</div>
				<div></div>
			</template>
			<template #footer>
				<div class="justify-content-end flex">
					<Button :label="props.submitText" @click="onSubmit"></Button>
				</div>
			</template>
		</Card>
	</div>
</template>

<script lang="ts" setup>
import {
	TournamentForm,
	tournamentFormClientToServer,
} from "@/interfaces/tournament"
import { useI18n } from "vue-i18n"

import { useForm } from "vee-validate"
import Card from "primevue/card"

import { array, boolean, date, object, string } from "yup"
import { toTypedSchema } from "@vee-validate/yup"

const { t } = useI18n({ inheritLocale: true })

const props = withDefaults(
	defineProps<{
		submitText: string
		disabled: boolean
		data: TournamentForm
	}>(),
	{
		disabled: false,
	},
)

// TODO: internalization
const { values, defineInputBinds, errors, defineComponentBinds, handleSubmit } =
	useForm({
		validationSchema: toTypedSchema(
			object({
				name: string()
					.required("validation.field_required")
					.min(4, "validation.field_too_short"),
				visible: boolean(),
				description: string().max(255),
				registration_phase: array()
					.of(date())
					.length(2, "validation.only_one_date")
					.required(),
				// TODO: check if game_phase is after registration_phase
				game_phase: array()
					.of(date())
					.length(2, "validation.only_one_date")
					.required(),
			}),
		),
		initialValues: {
			name: props.data.name,
			visible: props.data.visible,
			description: props.data.description,
			registration_phase: props.data.registration_phase,
			game_phase: props.data.game_phase,
		},
	})

const name = defineInputBinds("name")
const visible = defineComponentBinds("visible")
const description = defineInputBinds("description")

const registration_phase = defineComponentBinds("registration_phase")
const game_phase = defineComponentBinds("game_phase")

const emit = defineEmits(["submit"])

const onSubmit = handleSubmit((values) => {
	emit("submit", tournamentFormClientToServer(<TournamentForm>values))
})
</script>

<style scoped>
#card {
	width: min(90dvw, 50rem);
}
</style>
