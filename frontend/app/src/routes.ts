import viewTournaments from "@/components/views/tournaments/ViewTournaments.vue"
import viewCompetitions from "@/components/views/competitions/ViewCompetitions.vue"
import viewCompetition from "@/components/views/competition/ViewCompetition.vue"
import viewPrepareMatches from "@/components/views/prepare/ViewPrepareMatches.vue"
import ViewAssignMatches from "@/components/views/prepare/assignMatches/ViewAssignMatches.vue"
import ViewScheduleMatches from "@/components/views/prepare/ViewScheduleMatches.vue"
import ViewEditTeams from "@/components/views/prepare/editTeams/ViewEditTeams.vue"
import ViewEditCompetition from "@/components/views/prepare/ViewEditCompetition.vue"

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
		path: "/tournament/:tourId/competition/:compId",
		name: "Competition",
		component: viewCompetition,
	},
	{
		path: "/tournament/:tourId/prepare/:compId?",
		name: "Edit competition",
		component: viewPrepareMatches,
		children: [
			{
				path: "settings",
				name: "settings",
				component: ViewEditCompetition,
				meta: { step: 1, reset: false },
			},
			{
				path: "editTeams",
				name: "editTeams",
				component: ViewEditTeams,
				meta: { step: 2, reset: true },
			},
			{
				path: "assignMatches",
				name: "assignMatches",
				component: ViewAssignMatches,
				meta: { step: 3, reset: true },
			},
			{
				path: "scheduleMatches",
				name: "scheduleMatches",
				component: ViewScheduleMatches,
				meta: { step: 4, reset: false },
			},
		],
	},
	{
		path: "/player/registration",
		name: "Player registration",
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
