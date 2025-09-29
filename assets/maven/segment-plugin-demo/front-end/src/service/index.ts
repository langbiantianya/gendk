import { Status } from '@sdh/types';
import { sdhFetch } from '@sdh/utils';
import { ValidatedPropertyValue } from '../interface';

function commonResolver<T>(p: Promise<{ status: Status; data: T }>) {
  return p
    .then(res => ({
      status: 'finished',
      data: res.data,
    }))
    .catch(err => ({
      status: 'error',
      data: err,
    }));
}

sdhFetch.init({
  /* @ts-ignore */
  getState: window.store?.getState,
});

export async function validateProperty(params: {
  globalAttributePaths: string[]
}) {
  return commonResolver<Record<string, Omit<ValidatedPropertyValue, 'entityInfo'>>>(sdhFetch.fetch({
    url: '/api/v3/horizon/v1/web/meta/attribute/validate/batch',
    method: 'POST',
    params,
  }))
}