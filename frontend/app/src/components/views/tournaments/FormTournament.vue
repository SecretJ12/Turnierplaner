<template>
	<div class="flex justify-content-center w-full">
		<Card id="card">
			<template #title>{{ t(props.header) }}</template>
			<template #content>
				<div class="formgrid grid">
					<div class="field col-12 flex w-full justify-content-end">
						<div style="flex-grow: 3">
							<label for="name">{{ t("general.name") }}</label>
							<InputText
								id="name"
								type="text"
								v-bind="name"
								maxlength="30"
								class="w-full"
								:class="{ 'p-invalid': errors.name }"
							/>
							<InlineMessage v-if="errors.name" class="mt-2"
								>{{ t(errors.name || "") }}
							</InlineMessage>
						</div>
						<div
							class="flex flex-column min-w-max flex-grow-0"
							style="padding-left: 1rem"
						>
							<label for="visible" class="text-900">{{
								t("TournamentSettings.visible")
							}}</label>
							<SelectButton
								v-bind="visible"
								unselectable="false"
								:options="[
									{ name: 'Nein', value: false },
									{ name: 'Ja', value: true },
								]"
								optionLabel="name"
								optionValue="value"
							/>
						</div>
					</div>
					<div class="field col-12">
						<label for="description">{{ t("general.description") }}</label>
						<Textarea
							id="description"
							type="text"
							rows="4"
							maxlength="255"
							class="w-full"
							v-bind="description"
						></Textarea>
					</div>
					<div class="field col-6">
						<label for="registration">{{
							t("TournamentSettings.registration_phase")
						}}</label>
						<Calendar
							id="registration"
							v-bind="registration_phase"
							selection-mode="range"
							:manual-input="false"
							class="w-full"
							:date-format="t('date_format')"
							show-time
							show-icon
							:class="{ 'p-invalid': errors.registration_phase }"
						/>
						<InlineMessage v-if="errors.registration_phase" class="mt-2"
							>{{ t(errors.registration_phase || "") }}
						</InlineMessage>
					</div>
					<div class="field col-6">
						<label for="game_phase">{{
							t("TournamentSettings.game_phase")
						}}</label>
						<Calendar
							id="game_phase"
							v-bind="game_phase"
							selection-mode="range"
							:manual-input="false"
							class="w-full"
							:date-format="t('date_format')"
							show-time
							show-icon
							:class="{ 'p-invalid': errors.game_phase }"
						/>
						<InlineMessage v-if="errors.game_phase" class="mt-2"
							>{{ t(errors.game_phase || "") }}
						</InlineMessage>
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

import { array, boolean, date, mixed, object, setLocale, string } from "yup"
import { toTypedSchema } from "@vee-validate/yup"

const { t } = useI18n({ inheritLocale: true })

const props = withDefaults(
	defineProps<{
		submitText: string
		disabled: boolean
		data: TournamentForm
		header: string
	}>(),
	{
		disabled: false,
	},
)

setLocale({
	mixed: {
		notNull: "validation.date_missing",
	},
})

const { defineInputBinds, errors, defineComponentBinds, handleSubmit } =
	useForm({
		validationSchema: toTypedSchema(
			object({
				name: string()
					.required("validation.field_required")
					.min(4, "validation.field_too_short"),
				visible: boolean(),
				description: string().max(255),
				registration_phase: array()
					.length(2, "validation.date_missing")
					.of(mixed().nonNullable())
					.of(date())
					.test(
						"correctDates",
						"TournamentSettings.wrong_dates",
						(arr, context) => {
							if (!context.parent.game_phase?.[0]) return true
							return (
								context.parent.registration_phase[1] <
								context.parent.game_phase[0]
							)
						},
					)
					.required("validation.date_missing"),
				game_phase: array()
					.length(2, "validation.date_missing")
					.of(mixed().nonNullable())
					.of(date())
					.test(
						"correctDates",
						"TournamentSettings.wrong_dates",
						(arr, context) => {
							if (!context.parent.registration_phase?.[1]) return true
							return (
								context.parent.registration_phase[1] <
								context.parent.game_phase[0]
							)
						},
					)
					.required("validation.date_missing"),
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
