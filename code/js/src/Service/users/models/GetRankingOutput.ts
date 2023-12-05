import { SirenModel } from "../../media/siren/SirenModel"

type RankingEntry = {
    id : number,
    username: string,
    gamesPlayed: number,
    wins: number,
    losses: number,
    points: number,
    rank: number
}

interface RankingInfoOutputModel {
    rankingTable : RankingEntry[]
}

export type GetRankingOutput = SirenModel<RankingInfoOutputModel>