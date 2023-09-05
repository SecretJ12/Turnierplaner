<template>
	<div class="flex justify-content-center w-full">
		<div id="card" class="card">
			<h3>{{ props.header }}</h3>
			<div class="formgrid grid">
				<div class="field col-12">
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
				<Divider align="left col-12">
					<b>{{ t("CompetitionSettings.tournament_settings") }}</b>
				</Divider>
				<div class="field col-12 md:col-6 md:mr-8">
					<label for="name">{{ t("CompetitionSettings.type") }}</label>
					<Dropdown
						v-bind="selectedTourType"
						:options="[
							{
								name: t('CompetitionSettings.knockout'),
								value: TourType.KNOCKOUT,
							},
							{
								name: t('CompetitionSettings.groups'),
								value: TourType.GROUPS,
							},
						]"
						optionLabel="name"
						optionValue="value"
						:placeholder="t(`CompetitionSettings.type`)"
						class="w-full"
					>
						<template #value="slotProps">
							<div v-if="slotProps.value" class="flex align-items-center">
								<span
									v-if="slotProps.value === 'GROUPS'"
									class="material-symbols-outlined mr-2"
									>groups</span
								>
								<span v-else class="material-symbols-outlined mr-2"
									>grid_view</span
								>
								<div v-if="slotProps.value === 'GROUPS'" class="mt-1">
									{{ t("CompetitionSettings.groups") }}
								</div>
								<div v-else class="mt-1">
									{{ t("CompetitionSettings.knockout") }}
								</div>
							</div>
							<span v-else>
								{{ slotProps.placeholder }}
							</span>
						</template>
						<template #option="slotProps">
							<div class="flex flex-row align-items-center">
								<span
									v-if="slotProps.option.value === 'GROUPS'"
									class="material-symbols-outlined mr-2"
									>groups</span
								>
								<span v-else class="material-symbols-outlined mr-2"
									>grid_view</span
								>
								<div class="mt-1">{{ slotProps.option.name }}</div>
							</div>
						</template>
					</Dropdown>
				</div>

				<div class="field col-6">
					<label for="name">{{ t("CompetitionSettings.mode") }}</label>
					<Dropdown
						v-bind="selectedTourMode"
						:options="[
							{ name: t('CompetitionSettings.single'), value: Mode.SINGLE },
							{ name: t('CompetitionSettings.double'), value: Mode.DOUBLE },
						]"
						optionLabel="name"
						optionValue="value"
						:placeholder="t(`CompetitionSettings.mode`)"
						class="w-full"
					>
						<template #value="slotProps">
							<div v-if="slotProps.value" class="flex align-items-center">
								<span
									v-if="slotProps.value === 'DOUBLE'"
									class="material-symbols-outlined mr-2"
								>
									group
								</span>
								<span v-else class="material-symbols-outlined mr-2"
									>person</span
								>
								<div v-if="slotProps.value === 'DOUBLE'" class="mt-1">
									{{ t("CompetitionSettings.double") }}
								</div>
								<div v-else class="mt-1">
									{{ t("CompetitionSettings.single") }}
								</div>
							</div>
							<span v-else>
								{{ slotProps.placeholder }}
							</span>
						</template>
						<template #option="slotProps">
							<div class="flex align-items-center">
								<span
									v-if="slotProps.option.value === 'DOUBLE'"
									class="material-symbols-outlined mr-2"
									>group</span
								>
								<span v-else class="material-symbols-outlined mr-2"
									>person</span
								>
								<div class="mt-1">{{ slotProps.option.name }}</div>
							</div>
						</template>
					</Dropdown>
				</div>

				<div
					v-if="values.mode === Mode.DOUBLE"
					class="field col-6 flex flex-column"
				>
					<label for="name">{{ t("CompetitionSettings.signup") }}</label>
					<Dropdown
						v-bind="signUp"
						:options="[
							{
								name: t('CompetitionSettings.individual'),
								value: SignUp.INDIVIDUAL,
							},
							{
								name: t('CompetitionSettings.together'),
								value: SignUp.TOGETHER,
							},
						]"
						optionLabel="name"
						optionValue="value"
						:placeholder="t(`CompetitionSettings.signup`)"
						class="w-full"
					>
						<template #value="slotProps">
							<div v-if="slotProps.value" class="flex align-items-center">
								<span
									v-if="slotProps.value == 'TOGETHER'"
									class="material-symbols-outlined mr-2"
								>
									group_work
								</span>
								<span v-else class="material-symbols-outlined mr-2"
									>workspaces</span
								>
								<div v-if="slotProps.value === 'TOGETHER'" class="mt-1">
									{{ t("CompetitionSettings.together") }}
								</div>
								<div v-else class="mt-1">
									{{ t("CompetitionSettings.individual") }}
								</div>
							</div>
							<span v-else>
								{{ slotProps.placeholder }}
							</span>
						</template>
						<template #option="slotProps">
							<div class="flex align-items-center">
								<span
									v-if="slotProps.option.value === 'TOGETHER'"
									class="material-symbols-outlined mr-2"
									>group_work</span
								>
								<span v-else class="material-symbols-outlined mr-2"
									>workspaces</span
								>
								<div>{{ slotProps.option.name }}</div>
							</div>
						</template>
					</Dropdown>
				</div>

				<Divider align="left">
					<b>{{ t("CompetitionSettings.player") }}</b>
				</Divider>
				<div class="field col-12 flex flex-row align-items-center">
					<Checkbox v-bind="playerB_different" inputId="playerB_different_input" id="playerB_different" :binary="true" />
					<label for="playerB_different_input" class="ml-2 mb-0 mt-1">{{
							t("CompetitionSettings.differentB")
						}}</label>
				</div>

				<div :class="{ 'col-12': !values.playerB_different, 'col-6': values.playerB_different }">
					<Divider class="col-6" align="left">
						{{ t("CompetitionSettings.playerA") }}
					</Divider>
				</div>
				<div v-if="values.playerB_different" class="col-6">
					<Divider class="col-6" align="left">
						{{ t("CompetitionSettings.playerB") }}
					</Divider>
				</div>

				<div class="field flex flex-column" :class="{ 'col-12': !values.playerB_different, 'col-6': values.playerB_different }">
					<Dropdown
						v-bind="playerASex"
						:options="[
							{ name: t('CompetitionSettings.male'), value: Sex.MALE },
							{ name: t('CompetitionSettings.female'), value: Sex.FEMALE },
							{ name: t('CompetitionSettings.any'), value: Sex.ANY },
						]"
						optionLabel="name"
						optionValue="value"
						:placeholder="t(`CompetitionSettings.sex`)"
						class="w-full"
					>
						<template #option="slotProps">
							<div>
								{{ slotProps.option.name }}
							</div>
						</template>
					</Dropdown>
				</div>
				<div v-if="values.playerB_different" class="field col-6 flex flex-column">
					<Dropdown
						v-bind="playerBSex"
						:options="[
								{ name: t('CompetitionSettings.male'), value: Sex.MALE },
								{ name: t('CompetitionSettings.female'), value: Sex.FEMALE },
								{ name: t('CompetitionSettings.any'), value: Sex.ANY },
							]"
						optionLabel="name"
						optionValue="value"
						:placeholder="t(`CompetitionSettings.sex`)"
						class="w-full"
					>
						<template #option="slotProps">
							<div>{{ slotProps.option.name }}</div>
						</template>
					</Dropdown>
				</div>

				<div class="field flex flex-column" :class="{ 'col-12': !values.playerB_different, 'col-6': values.playerB_different }">
					<label for="minAge" class="text-900">{{
						t("CompetitionSettings.minAge")
					}}</label>
					<div class="flex flex-row align-items-center gap-2">
						<Checkbox
							id="minAge"
							v-bind="playerAHasMinAge"
							:binary="true"
						/>

						<Calendar
							id="minAge"
							v-bind="playerAMinAge"
							:manualInput="false"
							class="w-full"
							:dateFormat="t('date_format')"
							show-icon
							:disabled="!playerAHasMinAge.modelValue"
						/>
					</div>
				</div>
				<div v-if="values.playerB_different" class="field flex flex-column col-6">
					<label for="minAge" class="text-900">{{
							t("CompetitionSettings.minAge")
						}}</label>
					<div class="flex flex-row align-items-center gap-2">
						<Checkbox
							id="minAge"
							v-bind="playerBHasMinAge"
							:binary="true"
						/>

						<Calendar
							id="minAge"
							v-bind="playerBMinAge"
							class="w-full"
							:manualInput="false"
							:dateFormat="t('date_format')"
							show-icon
							:disabled="!playerBHasMinAge.modelValue"
						/>
					</div>
				</div>

				<div class="field flex flex-column" :class="{ 'col-12': !values.playerB_different, 'col-6': values.playerB_different }">
					<label for="minAge" class="text-900">{{
							t("CompetitionSettings.maxAge")
						}}</label>
					<div class="flex flex-row align-items-center gap-2">
						<Checkbox
							id="minAge"
							v-bind="playerAHasMaxAge"
							:binary="true"
						/>

						<Calendar
							id="minAge"
							v-bind="playerAMaxAge"
							class="w-full"
							:manualInput="false"
							:dateFormat="t('date_format')"
							show-icon
							:disabled="!playerAHasMaxAge.modelValue"
						/>
					</div>
				</div>
				<div v-if="values.playerB_different" class="field flex flex-column col-6">
					<label for="minAge" class="text-900">{{
							t("CompetitionSettings.maxAge")
						}}</label>
					<div class="flex flex-row align-items-center gap-2">
						<Checkbox
							id="minAge"
							v-bind="playerBHasMaxAge"
							:binary="true"
						/>

						<Calendar
							id="minAge"
							v-bind="playerBMaxAge"
							class="w-full"
							:manualInput="false"
							:dateFormat="t('date_format')"
							show-icon
							:disabled="!playerBHasMaxAge.modelValue"
						/>
					</div>
				</div>

				<div class="field col-12">
					<Button :label="props.submitText" @click="onSubmit"></Button>
				</div>
			</div>
		</div>
	</div>
</template>

<script lang="ts" setup>
import { useI18n } from "vue-i18n"
import {
	Competition,
	CompetitionForm,
	competitionFormToServer,
	Mode,
	Sex,
	SignUp,
	TourType,
} from "@/interfaces/competition"

import { useForm } from "vee-validate"

import { boolean, date, mixed, object, string } from "yup"
import { toTypedSchema } from "@vee-validate/yup"

const { t } = useI18n({ inheritLocale: true })

const props = withDefaults(
	defineProps<{
		submitText: string
		disabled: boolean
		competition: Competition
		header: string
	}>(),
	{
		disabled: false,
	},
)

const {
	values,
	defineInputBinds,
	errors,
	defineComponentBinds,
	handleSubmit,
	setFieldValue,
} = useForm({
	validationSchema: toTypedSchema(
		object({
			name: string()
				.min(4, t("validation.field_too_short"))
				.max(40, "validation.field_too_long")
				.required(t("validation.field_required")),
			description: string().max(50),
			tourType: mixed().oneOf(Object.values(TourType)).required(),
			mode: mixed().oneOf(Object.values(Mode)).required(),
			signUp: mixed().oneOf(Object.values(SignUp)).required(),

			playerA_Sex: mixed().oneOf(Object.values(Sex)).required(),
			playerA_hasMinAge: boolean(),
			playerA_minAge: date(),
			playerA_hasMaxAge: boolean(),
			playerA_maxAge: date(),

			playerB_different: boolean(),
			playerB_Sex: mixed().oneOf(Object.values(Sex)),
			playerB_hasMinAge: boolean(),
			playerB_minAge: date(),
			playerB_hasMaxAge: boolean(),
			playerB_maxAge: date(),
		}),
	),
	initialValues: {
		name: props.competition.name ? props.competition.name : undefined,
		description: props.competition.description,
		tourType: props.competition.tourType,
		mode: props.competition.mode,
		signUp: props.competition.signUp,
		playerA_Sex: props.competition.playerA.sex,
		playerA_hasMinAge: props.competition.playerA.hasMinAge,
		playerA_minAge: props.competition.playerA.minAge
			? props.competition.playerA.minAge
			: new Date(),
		playerA_hasMaxAge: props.competition.playerA.hasMaxAge,
		playerA_maxAge: props.competition.playerA.maxAge
			? props.competition.playerA.maxAge
			: new Date(),
		playerB_different: props.competition.playerB.different,
		playerB_Sex: props.competition.playerB.sex,
		playerB_hasMinAge: props.competition.playerB.hasMinAge,
		playerB_minAge: props.competition.playerB.minAge
			? props.competition.playerB.minAge
			: new Date(),
		playerB_hasMaxAge: props.competition.playerB.hasMaxAge,
		playerB_maxAge: props.competition.playerB.maxAge
			? props.competition.playerB.maxAge
			: new Date(),
	},
})

const name = defineInputBinds("name")
const description = defineInputBinds("description")
const selectedTourType = defineComponentBinds("tourType")
const selectedTourMode = defineComponentBinds("mode")
const signUp = defineComponentBinds("signUp")
const playerB_different = defineComponentBinds("playerB_different")
const playerASex = defineComponentBinds("playerA_Sex")
const playerBSex = defineComponentBinds("playerB_Sex")
const playerAHasMinAge = defineComponentBinds("playerA_hasMinAge")
const playerBHasMinAge = defineComponentBinds("playerB_hasMinAge")
const playerAHasMaxAge = defineComponentBinds("playerA_hasMaxAge")
const playerBHasMaxAge = defineComponentBinds("playerB_hasMaxAge")
const playerAMinAge = defineComponentBinds("playerA_minAge")
const playerAMaxAge = defineComponentBinds("playerA_maxAge")
const playerBMinAge = defineComponentBinds("playerB_minAge")
const playerBMaxAge = defineComponentBinds("playerB_maxAge")

const emit = defineEmits(["submit"])

const onSubmit = handleSubmit((values) => {
	emit(
		"submit",
		competitionFormToServer(
			<CompetitionForm>values,
			<string | null>props.competition.id,
		),
	)
})
</script>

<style scoped>
#card {
	width: min(90dvw, 50rem);
}
</style>