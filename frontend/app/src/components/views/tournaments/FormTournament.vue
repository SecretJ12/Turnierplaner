<template>
	<!--TODO add internalization-->
	<div class="justify-content-center" style="width: 100%">
		<div class="card">
			<h3>Tournament registration</h3>
			<div class="formgrid grid">
				<div class="field col-11">
					<label for="name">{{ t("general.name") }}</label>
					<input
						id="name"
						type="text"
						v-bind="name"
						maxlength="30"
						class="text-base text-color surface-overlay p-2 border-1 border-solid surface-border border-round appearance-none outline-none focus:border-primary w-full"
						rules="required|min:3"
					/>
					<small id="name-error" class="p-error">{{ errors.name }}</small>
				</div>
				<div class="field col flex flex-column">
					<label for="visible" class="text-900">{{
						t("TournamentSettings.visible")
					}}</label>
					<InputSwitch id="visible" v-bind="visible" />
				</div>
				<div class="field col-12">
					<label for="description">{{ t("general.description") }}</label>
					<textarea
						id="description"
						type="text"
						rows="4"
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
						show-icon
					/>
					<small id="registration_phase-error" class="p-error">{{
						errors.registration_phase
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
						show-icon
					/>
					<small id="game_phase-error" class="p-error">{{
						errors.game_phase
					}}</small>
				</div>
				<div class="field col">
					<Button :label="props.submitText" @click="onSubmit"></Button>
				</div>
			</div>
		</div>
	</div>
</template>

<script lang="ts" setup>
import { reactive, ref } from "vue"
import {
	TournamentForm,
	tournamentFormClientToServer,
	TournamentServer,
} from "@/interfaces/tournament"
import { useI18n } from "vue-i18n"

import { useForm, useFieldArray, useField, defineRule } from "vee-validate"
import Button from "primevue/button"

import { object, string, number, date, InferType, boolean, array } from "yup"
import { toTypedSchema } from "@vee-validate/yup"

const { t } = useI18n({ inheritLocale: true })

import { setLocale } from "yup"

const { values, defineInputBinds, errors, defineComponentBinds, handleSubmit } =
	useForm({
		validationSchema: toTypedSchema(
			object({
				name: string()
					.min(4, t("validation.field_too_short"))
					.required(t("validation.field_required")),
				visible: boolean(),
				description: string().max(500),
				registration_phase: array()
					.of(date())
					.length(2, "please choose two dates")
					.required(),
				game_phase: array()
					.of(date())
					.length(2, "please choose two dates")
					.required(),
			}),
		),
	})

const name = defineInputBinds("name")
const visible = defineComponentBinds("visible")
const description = defineInputBinds("description")

const registration_phase = defineComponentBinds("registration_phase")
const game_phase = defineComponentBinds("game_phase")

const formRef = ref<HTMLFormElement>()
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

const emit = defineEmits(["submit"])

function submit(formRef: HTMLFormElement | undefined) {
	if (!formRef) return
	formRef.validate((valid: boolean) => {
		if (valid) {
			const server_data: TournamentServer = tournamentFormClientToServer(
				props.data,
			)
			emit("submit", server_data)
		} else {
			console.log("validation failed")
		}
	})
}

const onSubmit = handleSubmit((values) => {
	console.log(values)
	emit("submit", values)
})
const checkDates = (
	rule: any, // eslint-disable-line @typescript-eslint/no-explicit-any
	value: Date,
	callback: (arg0?: Error) => void,
) => {
	if (!value) {
		callback(new Error(t("TournamentSettings.missing_date")))
		return
	}
	if (
		props.data.registration_phase === null ||
		props.data.game_phase === null
	) {
		callback()
		return
	}
	if (
		new Date(props.data.registration_phase[1]) >
		new Date(props.data.game_phase[0])
	)
		callback(new Error(t("TournamentSettings.wrong_dates")))
	else callback()
}

function validateField(value: string) {
	console.log(value)
	if (!value) {
		return "Name - Surname is required."
	}

	return true
}
</script>

<style scoped></style>
