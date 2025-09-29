import React, { ReactNode } from 'react';
import { NumberOutlined, StringOutlined, DatetimeOutlined, ListOutlined, BoolOutlined, FileUnknownOutlined } from '@sensd/icons';
import { DataType } from '@sdh/types';

const DataTypeIcons = {
  STRING: <StringOutlined style={{fontSize: '16px'}} />,
  NUMBER: <NumberOutlined style={{fontSize: '16px'}} />,
  DATETIME: <DatetimeOutlined style={{fontSize: '16px'}} />,
  LIST: <ListOutlined style={{fontSize: '16px'}} />,
  BOOL: <BoolOutlined style={{fontSize: '16px'}} />,
  BIGINT: <NumberOutlined style={{fontSize: '16px'}} />,
} as {
  [key in DataType]: ReactNode;
};

export function dataTypeIcon(dataType?: DataType) {
  return dataType ? DataTypeIcons[dataType] : <FileUnknownOutlined style={{fontSize: '16px'}} />;
}

export default DataTypeIcons;
