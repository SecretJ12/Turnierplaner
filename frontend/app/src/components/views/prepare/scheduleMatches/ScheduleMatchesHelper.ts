import { AnnotatedMatch } from "@/interfaces/match"
import { KnockoutMatch, KnockoutSystem } from "@/interfaces/knockoutSystem"
import { knockoutTitle } from "@/components/views/competition/knockoutSystem/KnockoutTitleGenerator"
import { GroupMatch, GroupSystem } from "@/interfaces/groupSystem"
import { CalEvent } from "@/calendar/CalendarInterfaces"

export type MatchCalEvent = CalEvent<AnnotatedMatch>

export function extractKnockoutMatches(
	knockout: KnockoutSystem,
	t: (_: string) => string,
	add: (match: KnockoutMatch, title: string) => void,
) {
	const rounds: KnockoutMatch[][] = []
	let queue: KnockoutMatch[] = []
	let nQueue: KnockoutMatch[] = []
	if (knockout.finale) queue.push(knockout.finale)
	if (knockout.thirdPlace) queue.push(knockout.thirdPlace)

	while (queue.length) {
		queue.forEach((match) => {
			if (match.prevMatch) {
				nQueue.push(match.prevMatch.a)
				nQueue.push(match.prevMatch.b)
			}
		})
		if (nQueue.length) rounds.push(nQueue)
		queue = nQueue
		nQueue = []
	}

	rounds.toReversed().forEach((round, i) => {
		round.forEach((match) => {
			if (i > 0 || (match.teamA && match.teamB))
				add(match, knockoutTitle(t)(i, rounds.length + 1))
		})
	})
	if (knockout.thirdPlace)
		add(knockout.thirdPlace, t("ViewKnockout.thirdPlace"))
	if (knockout.finale) add(knockout.finale, t("ViewKnockout.finale"))
}

export function extractGroupMatches(
	groupSystem: GroupSystem,
	t: (_: string) => string,
	add: (match: GroupMatch, title: string) => void,
) {
	groupSystem.groups.forEach((group, i) => {
		group.matches.forEach((match) => {
			add(match, t("ViewGroupSystem.group") + " " + (i + 1))
		})
	})
	extractKnockoutMatches(
		{ finale: groupSystem.finale, thirdPlace: groupSystem.thirdPlace },
		t,
		add,
	)
}
