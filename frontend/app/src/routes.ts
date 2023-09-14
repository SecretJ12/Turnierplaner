import viewTournaments from "@/components/views/tournaments/ViewTournaments.vue"
import viewCompetitions from "@/components/views/competitions/ViewCompetitions.vue"
import viewCompetition from "@/components/views/competition/ViewCompetition.vue"
import viewPrepareMatches from "@/components/views/prepare/ViewPrepareMatches.vue"
import ViewEditPlayer from "@/components/views/prepare/ViewEditPlayer.vue"
import ViewChooseMode from "@/components/views/prepare/ViewSelectType.vue"
import ViewAssignTeams from "@/components/views/prepare/assignTeams/ViewAssignTeams.vue"
import ViewAssignMatches from "@/components/views/prepare/ViewAssignMatches.vue"
import ViewScheduleMatches from "@/components/views/prepare/ViewScheduleMatches.vue"

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
		path: "/tournament/:tourId/prepare/:compId?",
		name: "Prepare matches",
		component: viewPrepareMatches,
		children: [
			{
				path: "editPlayers",
				name: "editPlayers",
				component: ViewEditPlayer,
				meta: { step: 1 },
			},
			{
				path: "selectType",
				name: "selectType",
				component: ViewChooseMode,
				meta: { step: 2 },
			},
			{
				path: "assignTeams",
				name: "assignTeams",
				component: ViewAssignTeams,
				meta: { step: 3 },
			},
			{
				path: "assignMatches",
				name: "assignMatches",
				component: ViewAssignMatches,
				meta: { step: 4 },
			},
			{
				path: "scheduleMatches",
				name: "scheduleMatches",
				component: ViewScheduleMatches,
				meta: { step: 5 },
			},
		],
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
