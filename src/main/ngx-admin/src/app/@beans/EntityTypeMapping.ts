import {EntityType} from "./EntityType";

export class EntityTypeMapping {
    private readonly entity: EntityType;
    private readonly valueMapping: (val: string) => string;

    constructor(entity: EntityType, mapping: (val: string) => string) {
        this.entity = entity;
        this.valueMapping = mapping;
    }

    public getEntity(): EntityType {
        return this.entity;
    }

    public getValueUsingMapping(input: string): string {
        return this.valueMapping(input);
    }
}