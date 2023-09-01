<template>
	<div class="flex justify-content-center w-full">
		<div class="card" id="card">
			<h3>Tournament registration</h3>
			<div class="formgrid grid">
				<div class="field col-12">
					<label for="name">{{ t("general.name") }}</label>
					<input
						id="name"
						type="text"
						v-bind="name"
						maxlength="30"
						class="text-base text-color surface-overlay p-2 border-1 border-solid surface-border border-round appearance-none outline-none focus:border-primary w-full"
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
				<Divider align="left">
					<b>{{ t("CompetitionSettings.tournament_settings") }}</b>
				</Divider>
				<div class="field col-12 flex flex-column">
					<!--						<label for="tournamentType">Placeholder </label>-->
					<Dropdown
						v-bind="selectedTourType"
						:options="tourTypes"
						optionLabel="name"
						:placeholder="t(`CompetitionSettings.type`)"
						class="w-full md:w-14rem"
					>
						<template #value="slotProps">
							<div v-if="slotProps.value" class="flex align-items-center">
								<span
									v-if="slotProps.value.name == 'GROUPS'"
									class="material-symbols-outlined"
									>groups</span
								>
								<span v-else class="material-symbols-outlined">grid_view</span>
								<div>{{ slotProps.value.name }}</div>
							</div>
							<span v-else>
								{{ slotProps.placeholder }}
							</span>
						</template>
						<template #option="slotProps">
							<div class="flex align-items-center">
								<span
									v-if="slotProps.option.name === 'GROUPS'"
									class="material-symbols-outlined"
									>groups</span
								>
								<span v-else class="material-symbols-outlined">grid_view</span>
								<div>{{ slotProps.option.name }}</div>
							</div>
						</template>
					</Dropdown>
				</div>

				<div class="field col-12 flex flex-column">
					<Dropdown
						v-bind="selectedTourMode"
						:options="tourMode"
						optionLabel="name"
						:placeholder="t(`CompetitionSettings.mode`)"
						class="w-full md:w-14rem"
					>
						<template #value="slotProps">
							<div v-if="slotProps.value" class="flex align-items-center">
								<span
									v-if="slotProps.value.name == 'DOUBLE'"
									class="material-symbols-outlined"
									>group</span
								>
								<span v-else class="material-symbols-outlined">person</span>
								<div>{{ slotProps.value.name }}</div>
							</div>
							<span v-else>
								{{ slotProps.placeholder }}
							</span>
						</template>
						<template #option="slotProps">
							<div class="flex align-items-center">
								<span
									v-if="slotProps.option.name === 'DOUBLE'"
									class="material-symbols-outlined"
									>group</span
								>
								<span v-else class="material-symbols-outlined">person</span>
								<div>{{ slotProps.option.name }}</div>
							</div>
						</template>
					</Dropdown>
				</div>

				<div v-if="selectedTourMode.modelValue?.name === Mode.DOUBLE">
					<div class="field col-12 flex flex-column">
						<Dropdown
							v-bind="signUp"
							:options="signUpOptions"
							optionLabel="name"
							:placeholder="t(`CompetitionSettings.signup`)"
							class="w-full md:w-14rem"
						>
							<template #value="slotProps">
								<div v-if="slotProps.value" class="flex align-items-center">
									<span
										v-if="slotProps.value.name == 'DOUBLE'"
										class="material-symbols-outlined"
										>group</span
									>
									<span v-else class="material-symbols-outlined">person</span>
									<div>{{ slotProps.value.name }}</div>
								</div>
								<span v-else>
									{{ slotProps.placeholder }}
								</span>
							</template>
							<template #option="slotProps">
								<div class="flex align-items-center">
									<span
										v-if="slotProps.option.name === 'DOUBLE'"
										class="material-symbols-outlined"
										>group</span
									>
									<span v-else class="material-symbols-outlined">person</span>
									<div>{{ slotProps.option.name }}</div>
								</div>
							</template>
						</Dropdown>
					</div>
				</div>
				<Divider align="left">
					<b>{{ t("CompetitionSettings.player") }}</b>
				</Divider>
				<Divider v-if="playerB_different.modelValue" align="left">
					<!--	TODO internalization	-->
					Spieler 1
				</Divider>
				<div class="field col-12 flex flex-column">
					<Dropdown
						v-bind="playerASex"
						:options="sexOptions"
						optionLabel="name"
						:placeholder="t(`CompetitionSettings.sex`)"
						class="w-full md:w-14rem"
					>
						<template #value="slotProps">
							<div v-if="slotProps.value" class="flex align-items-center">
								<span
									v-if="slotProps.value.name == 'FEMALE'"
									class="material-symbols-outlined"
									>female</span
								>
								<span v-else class="material-symbols-outlined">male</span>
								<div>{{ slotProps.value.name }}</div>
							</div>
							<span v-else>
								{{ slotProps.placeholder }}
							</span>
						</template>
						<template #option="slotProps">
							<div class="flex align-items-center">
								<span
									v-if="slotProps.option.name === 'FEMALE'"
									class="material-symbols-outlined"
									>female</span
								>
								<span v-else class="material-symbols-outlined">male</span>
								<div>{{ slotProps.option.name }}</div>
							</div>
						</template>
					</Dropdown>
				</div>

				<div class="field col-12 flex flex-column">
					<label for="minAge" class="text-900">{{
						t("CompetitionSettings.minAge")
					}}</label>
					<div>
						<InputSwitch
							v-if="!playerAHasMinAge.modelValue"
							id="minAge"
							v-bind="playerAHasMinAge"
						>
						</InputSwitch>

						<Calendar
							id="minAge"
							v-bind="playerAMinAge"
							:manualInput="false"
							dateFormat="dd/mm/yy"
							show-icon
							v-if="playerAHasMinAge.modelValue"
						/>
						<Button
							v-if="playerAHasMinAge.modelValue"
							@click="updateAge(1)"
							rounded
							text
						>
							<template #icon>
								<span class="material-icons">cancel</span>
							</template>
						</Button>
					</div>
				</div>

				<div class="field col-12 flex flex-column">
					<label for="maxAge" class="text-900">{{
						t("CompetitionSettings.maxAge")
					}}</label>
					<div>
						<InputSwitch
							v-if="!playerAHasMaxAge.modelValue"
							id="maxAge"
							v-bind="playerAHasMaxAge"
						>
						</InputSwitch>
						<Calendar
							id="minAge"
							v-bind="playerAMaxAge"
							:manualInput="false"
							dateFormat="dd/mm/yy"
							show-icon
							v-if="playerAHasMaxAge.modelValue"
						/>
						<Button
							v-if="playerAHasMaxAge.modelValue"
							@click="updateAge(2)"
							rounded
							text
						>
							<template #icon>
								<span class="material-icons">cancel</span>
							</template>
						</Button>
					</div>
				</div>

				<div class="field col-12 flex flex-column">
					<label for="different" class="text-900">{{
						t("CompetitionSettings.differentB")
					}}</label>
					<InputSwitch id="different" v-bind="playerB_different"> </InputSwitch>
				</div>

				<div v-if="playerB_different.modelValue">
					<!-- TODO internalization-->
					<Divider align="left"> Spieler 2 </Divider>
					<div class="field col-12 flex flex-column">
						<Dropdown
							v-bind="playerBSex"
							:options="sexOptions"
							optionLabel="name"
							:placeholder="t(`CompetitionSettings.sex`)"
							class="w-full md:w-14rem"
						>
							<template #value="slotProps">
								<div v-if="slotProps.value" class="flex align-items-center">
									<span
										v-if="slotProps.value.name == 'FEMALE'"
										class="material-symbols-outlined"
										>female</span
									>
									<span v-else class="material-symbols-outlined">male</span>
									<div>{{ slotProps.value.name }}</div>
								</div>
								<span v-else>
									{{ slotProps.placeholder }}
								</span>
							</template>
							<template #option="slotProps">
								<div class="flex align-items-center">
									<span
										v-if="slotProps.option.name === 'FEMALE'"
										class="material-symbols-outlined"
										>female</span
									>
									<span v-else class="material-symbols-outlined">male</span>
									<div>{{ slotProps.option.name }}</div>
								</div>
							</template>
						</Dropdown>
					</div>

					<div class="field col-12 flex flex-column">
						<label for="minAge" class="text-900">{{
							t("CompetitionSettings.minAge")
						}}</label>
						<div>
							<InputSwitch
								id="minAge"
								v-bind="playerBHasMinAge"
								v-if="!playerBHasMinAge.modelValue"
							>
							</InputSwitch>
							<Calendar
								id="minAge"
								v-bind="playerBMinAge"
								:manualInput="false"
								dateFormat="dd/mm/yy"
								show-icon
								v-if="playerBHasMinAge.modelValue"
							/>
							<Button
								v-if="playerBHasMinAge.modelValue"
								@click="updateAge(3)"
								rounded
								text
							>
								<template #icon>
									<span class="material-icons">cancel</span>
								</template>
							</Button>
						</div>
					</div>
					<div class="field col-12 flex flex-column">
						<label for="maxAge" class="text-900">{{
							t("CompetitionSettings.maxAge")
						}}</label>
						<div>
							<InputSwitch
								v-if="!playerBHasMaxAge.modelValue"
								id="maxAge"
								v-bind="playerBHasMaxAge"
							>
							</InputSwitch>
							<Calendar
								id="minAge"
								v-bind="playerBMaxAge"
								:manualInput="false"
								dateFormat="dd/mm/yy"
								show-icon
								v-if="playerBHasMaxAge.modelValue"
							/>
							<Button
								v-if="playerBHasMaxAge.modelValue"
								@click="updateAge(4)"
								rounded
								text
							>
								<template #icon>
									<span class="material-icons">cancel</span>
								</template>
							</Button>
						</div>
					</div>
				</div>
				<div class="field col-12">
					<Button :label="props.submitText" @click="onSubmit"> </Button>
				</div>
			</div>
		</div>
	</div>
</template>

<script lang="ts" setup>
import { ref } from "vue"
import { useI18n } from "vue-i18n"
import {
	Competition,
	competitionForm,
	competitionFormToServer,
	Mode,
	Sex,
	TourType,
} from "@/interfaces/competition"

import { useForm } from "vee-validate"

import { boolean, date, mixed, object, string } from "yup"
import { toTypedSchema } from "@vee-validate/yup"

const { t } = useI18n({ inheritLocale: true })

// TODO use primeflex responsive grid
let windowWidth = ref(window.innerWidth)
window.addEventListener("resize", () => {
	windowWidth.value = window.innerWidth
})

const formRef = ref()
const props = withDefaults(
	defineProps<{
		submitText: string
		disabled: boolean
		competition: Competition
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
			tourType: object({
				name: mixed().oneOf(Object.values(TourType)).required(),
			}),
			mode: object({ name: mixed().oneOf(Object.values(Mode)).required() }),
			signUp: object({ name: string() }).required(),

			playerA_Sex: object({
				name: mixed().oneOf(Object.values(Sex)).required(),
			}),

			playerA_hasMinAge: boolean(),
			playerA_minAge: date(),
			playerA_hasMaxAge: boolean(),
			playerA_maxAge: date(),

			playerB_different: boolean(),
			playerB_Sex: object({
				name: string().oneOf(Object.values(Sex)).required(),
			}),
			playerB_hasMinAge: boolean(),
			playerB_minAge: date(),
			playerB_hasMaxAge: boolean(),
			playerB_maxAge: date(),
		}),
	),
	initialValues: {
		name: props.competition.name ? props.competition.name : undefined,
		description: props.competition.description,
		tourType: { name: props.competition.tourType },
		mode: { name: props.competition.mode },
		signUp: { name: props.competition.signUp },
		playerA_Sex: { name: props.competition.playerA.sex },
		playerA_hasMinAge: props.competition.playerA.hasMinAge,
		playerA_minAge: props.competition.playerA.minAge
			? props.competition.playerA.minAge
			: undefined,
		playerA_hasMaxAge: props.competition.playerA.hasMaxAge,
		playerA_maxAge: props.competition.playerA.maxAge
			? props.competition.playerA.maxAge
			: undefined,
		playerB_different: props.competition.playerB.different,
		playerB_Sex: { name: props.competition.playerB.sex },
		playerB_hasMinAge: props.competition.playerB.hasMinAge,
		playerB_minAge: props.competition.playerB.minAge
			? props.competition.playerB.minAge
			: undefined,
		playerB_hasMaxAge: props.competition.playerB.hasMaxAge,
		playerB_maxAge: props.competition.playerB.maxAge
			? props.competition.playerB.maxAge
			: undefined,
	},
})

const name = defineInputBinds("name")
const description = defineInputBinds("description")

const tourTypes = ref([{ name: TourType.KNOCKOUT }, { name: TourType.GROUPS }])
const selectedTourType = defineComponentBinds("tourType")

const tourMode = ref([{ name: Mode.SINGLE }, { name: Mode.DOUBLE }])
const selectedTourMode = defineComponentBinds("mode")

const signUpOptions = ref([{ name: "zusammen" }, { name: "einzeln" }])
const signUp = defineComponentBinds("signUp")

const sexOptions = ref([{ name: Sex.MALE }, { name: Sex.FEMALE }])

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

function updateAge(slideNumber: number) {
	switch (slideNumber) {
		case 1:
			setFieldValue("playerA_hasMinAge", false)
			break
		case 2:
			setFieldValue("playerA_hasMaxAge", false)
			break
		case 3:
			setFieldValue("playerB_hasMinAge", false)
			break
		case 4:
			setFieldValue("playerB_hasMaxAge", false)
			break
	}
}

const onSubmit = handleSubmit((values) => {
	emit(
		"submit",
		competitionFormToServer(
			<competitionForm>values,
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
