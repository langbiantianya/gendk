import React, { forwardRef, useMemo, useCallback, useImperativeHandle } from 'react';
import { Form, Input, Button, Space } from 'sensd';
import _ from 'lodash';
import { MinusCircleOutlined, PlusOutlined } from '@sensd/icons';
import styles from './index.less';

type ExtParams = {
  attribute_path: string;
  config: {
    app_secret_key: string;
  }
}

type ChannelConfig = {
  attribute_paths: string[];
  app_secret_key: string;
}

interface UserFormIProps {
  value: any;
  onChange?: (value: any) => void;
  onStatusChange?: ({
    statusInfo = [],
  }: {
    statusInfo: {
      status: boolean;
      errorMsg: string;
    }[];
  }) => void;
  mode?: 'edit' | 'view';
  entityName?: string;
  [index: string]: any;
}

const UserForm = forwardRef((props: UserFormIProps, ref) => {
  const { value, mode, entityName, onStatusChange, onChange } = props;

  useImperativeHandle(
    ref,
    () => ({
      validateFields: async () => Promise.resolve(),
    }),
    [],
  );

  const valueObject = useMemo(() => {
    let _valueObject = {};
    try {
      _valueObject = JSON.parse(value) || {}
    } catch (error) {}
    return Object.keys(_valueObject).map((key) => {
      return {
        key,
        value: _valueObject[key],
      }
    });
  }, [value])

  const onChangeValue = useCallback(
    (__, newValue) => {
      onChange && onChange(JSON.stringify(_.fromPairs(newValue.extra.map((item) => [item?.key, item?.value]))));
    },
    [],
  )

  return (
    <div className={styles.userForm}>
    <Form disabled={mode === 'view'}  onValuesChange={onChangeValue}  autoComplete="off" initialValues={{extra: valueObject}} >
      <Form.List name="extra">
        {(fields, { add, remove }) => (
          <>
            {fields.map(({ key, name, ...restField }) => (
              <Space key={key} style={{ display: 'flex' }} align="baseline">
                <Form.Item
                  {...restField}
                  name={[name, 'key']}
                  rules={[{ required: true, message: 'Missing key' }]}
                  validateTrigger="onBlur"
                  hideErrorWhenChange
                >
                  <Input placeholder="key" />
                </Form.Item>
                <Form.Item
                  {...restField}
                  name={[name, 'value']}
                  rules={[{ required: true, message: 'Missing value' }]}
                  validateTrigger="onBlur"
                  hideErrorWhenChange
                >
                  <Input placeholder="value" />
                </Form.Item>
                <MinusCircleOutlined onClick={() => remove(name)} />
              </Space>
            ))}
            {mode !== 'view' && <Form.Item>
              <Button type="dashed" onClick={() => add()} block icon={<PlusOutlined />}>
                增加一项
              </Button>
            </Form.Item>}
          </>
        )}
      </Form.List>
    </Form>
    </div>
    )
});

export default UserForm;
