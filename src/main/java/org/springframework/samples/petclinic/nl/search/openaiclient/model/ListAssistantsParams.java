package org.springframework.samples.petclinic.nl.search.openaiclient.model;

import java.util.Optional;

/**
 * Query parameters for listing assistants
 *
 * @param limit A limit on the number of objects to be returned. Limit can range between 1
 * and 100, and the default is 20.
 * @param order Sort order by the created_at timestamp of the objects. asc for ascending
 * order and desc for descending order.
 * @param after A cursor for use in pagination. after is an object ID that defines your
 * place in the list. For instance, if you make a list request and receive 100 objects,
 * ending with obj_foo, your subsequent call can include after=obj_foo in order to fetch
 * the next page of the list.
 * @param before A cursor for use in pagination. before is an object ID that defines your
 * place in the list. For instance, if you make a list request and receive 100 objects,
 * ending with obj_foo, your subsequent call can include before=obj_foo in order to fetch
 * the previous page of the list.
 */
public record ListAssistantsParams(Optional<Integer> limit, Optional<ListOrder> order, Optional<String> after,
		Optional<String> before) {
}
