import viewTournaments from "@/components/views/tournaments/ViewTournaments.vue"
import viewCompetitions from "@/components/views/competitions/ViewCompetitions.vue"
import viewCompetition from "@/components/views/competition/ViewCompetition.vue"
import viewPrepareMatches from "@/components/views/prepare/ViewPrepareMatches.vue"

export default [
	{
		path: "/",
		name: "Tournaments",
		component: viewTournaments,
	},
	{
		path: "/createTournament",
		name: "Create tournament",
		component: () =>
			import("@/components/views/tournaments/ViewCreateTournament.vue"),
	},
	{
		path: "/tournament/:tourId/edit",
		name: "Edit tournament",
		component: () =>
			import("@/components/views/tournaments/ViewEditTournament.vue"),
	},
	{
		path: "/tournament/:tourId",
		name: "Competitions",
		component: viewCompetitions,
	},
	{
		path: "/tournament/:tourId/createCompetition",
		name: "Create competition",
		component: () =>
			import("@/components/views/competitions/ViewCreateCompetition.vue"),
	},
	{
		path: "/tournament/:tourId/competition/:compId/edit",
		name: "Edit competition",
		component: () =>
			import("@/components/views/competitions/ViewEditCompetition.vue"),
	},
	{
		path: "/tournament/:tourId/competition/:compId",
		name: "Competition",
		component: viewCompetition,
	},
	{
		path: "/tournament/:tourId/prepare/:step/:competition",
		name: "Prepare matches",
		component: viewPrepareMatches,
	},
	{
		path: "/tournament/:tourId/prepare",
		name: "Prepare all matches",
		component: viewPrepareMatches,
	},
	{
		path: "/player/registration",
		name: "Player Registration",
		component: () =>
			import("@/components/views/player/ViewPlayerRegistration.vue"),
	},
	{
		path: "/player/verification",
		name: "Player verified",
		component: () => import("@/components/views/player/ViewVerification.vue"),
	},
	{
		path: "/templates",
		name: "Templates",
		component: () => import("@/components/views/ViewTemplates.vue"),
	},
]
