import { City } from './city';

export interface Country {
    uid: string,
    name: string
    cities: City[];
}