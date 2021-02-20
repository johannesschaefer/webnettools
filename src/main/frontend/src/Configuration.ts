export interface Configuration {
    availableTools: string[];
    toolMD: ToolMD[];
}

export interface ToolMD {
    name: string;
    displayName: string;
    description: string;
    main: StringOptionMD;
    options: OptionMD[];
}

export interface OptionMD {
    name: string;
    displayName: string;
    type: string;
    description: string;
    defaultValue: string | number | boolean;
}

export interface BooleanOptionMD extends OptionMD {
    labelTrue: string;
    labelFalse: string;
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