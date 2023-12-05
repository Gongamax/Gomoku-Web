import { SirenModel } from '../../media/siren/SirenModel';

type Variant = {
  name: string;
  boardDim: number;
  playRule: string;
  openingRule: string;
};

export interface GetVariants {
  variants: Variant[];
}

export type GetVariantsOutput = SirenModel<GetVariants>;
