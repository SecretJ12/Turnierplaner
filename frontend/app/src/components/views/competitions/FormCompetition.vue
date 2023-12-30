<template>
	<div class="formgrid grid">
		<div class="field col-12">
			<label for="name">{{ t("general.name") }}</label>
			<InputText
				id="name"
				:class="{ 'p-invalid': errors.name }"
				:disabled="disabled"
				class="w-full"
				maxlength="30"
				type="text"
				v-bind="name"
			/>
			<InlineMessage v-if="errors.name" class="mt-2"
				>{{ t(errors.name || "") }}
			</InlineMessage>
		</div>
		<div class="field col-12">
			<label for="description">{{ t("general.description") }}</label>
			<textarea
				id="description"
				:disabled="disabled"
				class="text-base text-color surface-overlay p-2 border-1 border-solid surface-border border-round appearance-none outline-none focus:border-primary w-full"
				rows="4"
				type="text"
				v-bind="description"
			></textarea>
		</div>
		<Divider align="left">
			<b>{{ t("CompetitionSettings.tournament_settings") }}</b>
		</Divider>
		<div class="field col-12 md:col-6 md:mr-8">
			<label for="name">{{ t("CompetitionSettings.type") }}</label>
			<Dropdown
				:disabled="disabled"
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
				:placeholder="t(`CompetitionSettings.type`)"
				class="w-full"
				option-label="name"
				option-value="value"
				v-bind="selectedTourType"
			>
				<template #value="slotProps">
					<div v-if="slotProps.value" class="flex align-items-center">
						<span
							v-if="slotProps.value === 'GROUPS'"
							class="material-symbols-outlined mr-2"
							>groups</span
						>
						<span v-else class="material-symbols-outlined mr-2">grid_view</span>
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
						<span v-else class="material-symbols-outlined mr-2">grid_view</span>
						<div class="mt-1">{{ slotProps.option.name }}</div>
					</div>
				</template>
			</Dropdown>
		</div>

		<div class="field col-6">
			<label for="name">{{ t("CompetitionSettings.mode") }}</label>
			<Dropdown
				:disabled="disabled"
				:options="[
					{ name: t('CompetitionSettings.single'), value: Mode.SINGLE },
					{ name: t('CompetitionSettings.double'), value: Mode.DOUBLE },
				]"
				:placeholder="t(`CompetitionSettings.mode`)"
				class="w-full"
				option-label="name"
				option-value="value"
				v-bind="selectedTourMode"
			>
				<template #value="slotProps">
					<div v-if="slotProps.value" class="flex align-items-center">
						<span
							v-if="slotProps.value === 'DOUBLE'"
							class="material-symbols-outlined mr-2"
						>
							group
						</span>
						<span v-else class="material-symbols-outlined mr-2">person</span>
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
						<span v-else class="material-symbols-outlined mr-2">person</span>
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
				:disabled="disabled"
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
				:placeholder="t(`CompetitionSettings.signup`)"
				class="w-full"
				option-label="name"
				option-value="value"
				v-bind="signUp"
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
		<div class="field col-12 flex flex-row align-items-center mb-4">
			<Checkbox
				id="playerB_different"
				:binary="true"
				:disabled="disabled"
				input-id="playerB_different_input"
				v-bind="playerB_different"
			/>
			<label class="ml-2 mb-0 mt-1" for="playerB_different_input">{{
				t("CompetitionSettings.differentB")
			}}</label>
		</div>

		<div v-if="values.playerB_different" class="col-6">
			<Divider align="left" class="col-6">
				{{ t("CompetitionSettings.playerA") }}
			</Divider>
		</div>
		<div v-if="values.playerB_different" class="col-6">
			<Divider align="left" class="col-6">
				{{ t("CompetitionSettings.playerB") }}
			</Divider>
		</div>

		<div
			:class="{
				'col-12': !values.playerB_different,
				'col-6': values.playerB_different,
			}"
			class="field flex flex-column"
		>
			<label class="text-900" for="playerASex">{{
				t("CompetitionSettings.sex")
			}}</label>
			<Dropdown
				:disabled="disabled"
				:options="[
					{ name: t('CompetitionSettings.male'), value: Sex.MALE },
					{ name: t('CompetitionSettings.female'), value: Sex.FEMALE },
					{ name: t('CompetitionSettings.any'), value: Sex.ANY },
				]"
				:placeholder="t(`CompetitionSettings.sex`)"
				class="w-full"
				option-label="name"
				option-value="value"
				v-bind="playerASex"
			>
				<template #option="slotProps">
					<div>
						{{ slotProps.option.name }}
					</div>
				</template>
			</Dropdown>
		</div>
		<div v-if="values.playerB_different" class="field col-6 flex flex-column">
			<label class="text-900" for="playerBSex">{{
				t("CompetitionSettings.sex")
			}}</label>
			<Dropdown
				:disabled="disabled"
				:options="[
					{ name: t('CompetitionSettings.male'), value: Sex.MALE },
					{ name: t('CompetitionSettings.female'), value: Sex.FEMALE },
					{ name: t('CompetitionSettings.any'), value: Sex.ANY },
				]"
				:placeholder="t(`CompetitionSettings.sex`)"
				class="w-full"
				option-label="name"
				option-value="value"
				v-bind="playerBSex"
			>
				<template #option="slotProps">
					<div>{{ slotProps.option.name }}</div>
				</template>
			</Dropdown>
		</div>

		<div
			:class="{
				'col-12': !values.playerB_different,
				'col-6': values.playerB_different,
			}"
			class="field flex flex-column"
		>
			<label class="text-900" for="minAge">{{
				t("CompetitionSettings.minAge")
			}}</label>
			<div class="flex flex-row align-items-center gap-2">
				<Checkbox
					id="minAge"
					:binary="true"
					:disabled="disabled"
					v-bind="playerAHasMinAge"
				/>

				<Calendar
					id="minAge"
					:date-format="t('date_format')"
					:disabled="!playerAHasMinAge.modelValue || disabled"
					:manual-input="false"
					class="w-full"
					show-icon
					v-bind="playerAMinAge"
				/>
			</div>
		</div>
		<div v-if="values.playerB_different" class="field flex flex-column col-6">
			<label class="text-900" for="minAge">{{
				t("CompetitionSettings.minAge")
			}}</label>
			<div class="flex flex-row align-items-center gap-2">
				<Checkbox id="minAge" :binary="true" v-bind="playerBHasMinAge" />

				<Calendar
					id="minAge"
					:date-format="t('date_format')"
					:disabled="!playerBHasMinAge.modelValue || disabled"
					:manual-input="false"
					class="w-full"
					show-icon
					v-bind="playerBMinAge"
				/>
			</div>
		</div>

		<div
			:class="{
				'col-12': !values.playerB_different,
				'col-6': values.playerB_different,
			}"
			class="field flex flex-column"
		>
			<label class="text-900" for="minAge">{{
				t("CompetitionSettings.maxAge")
			}}</label>
			<div class="flex flex-row align-items-center gap-2">
				<Checkbox
					id="minAge"
					:binary="true"
					:disabled="disabled"
					v-bind="playerAHasMaxAge"
				/>

				<Calendar
					id="minAge"
					:date-format="t('date_format')"
					:disabled="!playerAHasMaxAge.modelValue || disabled"
					:manual-input="false"
					class="w-full"
					show-icon
					v-bind="playerAMaxAge"
				/>
			</div>
		</div>
		<div v-if="values.playerB_different" class="field flex flex-column col-6">
			<label class="text-900" for="minAge">{{
				t("CompetitionSettings.maxAge")
			}}</label>
			<div class="flex flex-row align-items-center gap-2">
				<Checkbox
					id="minAge"
					:binary="true"
					:disabled="disabled"
					v-bind="playerBHasMaxAge"
				/>

				<Calendar
					id="minAge"
					:date-format="t('date_format')"
					:disabled="!playerBHasMaxAge.modelValue || disabled"
					:manual-input="false"
					class="w-full"
					show-icon
					v-bind="playerBMaxAge"
				/>
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
		disabled: boolean
		competition: Competition
	}>(),
	{
		disabled: false,
	},
)

const { values, defineInputBinds, errors, defineComponentBinds, handleSubmit } =
	useForm<CompetitionForm>({
		validationSchema: toTypedSchema(
			object({
				name: string().min(4).max(40).required(),
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

const onSubmit = handleSubmit((values) => {
	emit(
		"submit",
		competitionFormToServer(
			<CompetitionForm>values,
			<string | null>props.competition.id,
		),
	)
})
defineExpose({ onSubmit })
const emit = defineEmits(["submit"])
</script>

<style scoped></style>
