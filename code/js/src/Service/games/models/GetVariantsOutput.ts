import { SirenModel } from '../../media/siren/SirenModel';

export type Variant = {
  name: string;
  board_dim: number;
  playRule: string;
  openingRule: string;
  points: number;
};

export interface GetVariants {
  variants: Variant[];
}

export type GetVariantsOutput = SirenModel<GetVariants>;
