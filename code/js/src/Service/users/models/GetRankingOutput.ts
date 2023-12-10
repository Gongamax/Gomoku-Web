import { SirenModel } from "../../media/siren/SirenModel"

type RankingEntry = {
    page: number,
    pageSize: number
}

interface RankingInfoOutputModel {
    rankingTable : RankingEntry[]
}

export type GetRankingOutput = SirenModel<RankingInfoOutputModel>