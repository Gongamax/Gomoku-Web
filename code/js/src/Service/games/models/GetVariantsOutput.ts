import { SirenModel } from '../../media/siren/SirenModel';

export type Variant = {
  name: string;
  boardDim: number;
  playRule: string;
  openingRule: string;
  points: number;
};

export interface GetVariants {
  variants: Variant[];
}

export type GetVariantsOutput = SirenModel<GetVariants>;
