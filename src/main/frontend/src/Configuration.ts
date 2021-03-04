export interface Configuration {
    toolMD: ToolMD[];
    introText?: string;
}

export interface ToolMD {
    name: string;
    displayName: string;
    description: string;
    main: StringOptionMD;
    options: OptionMD[];
    groups: GroupMD[];
    presetPayload: any;
}

export interface GroupMD {
    name: string;
    description: string;
}

export interface OptionMD {
    name: string;
    displayName: string;
    type: string;
    description: string;
    defaultValue: string | number | boolean;
    group: string;
}

export interface BooleanOptionMD extends OptionMD {
    labelTrue: string;
    labelFalse: string;
}

export interface EnumOptionMD extends OptionMD {
    values: { value: string, label: string }[];
}

export interface NumberOptionMD extends OptionMD {
    min: number;
    max: number;
    step: number;
}

export interface StringOptionMD extends OptionMD {
    minlength: number;
    maxlength: number;
}

export interface FileOptionMD extends OptionMD {
    maxSize: number;
    accept: string;
}