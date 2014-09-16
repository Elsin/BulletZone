package net.cserna.bence.bulletzone.entity;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Optional;

public class FieldHolder {

	private final Map<Direction, FieldHolder> neighbors = new HashMap<Direction, FieldHolder>();
	private Optional<FieldEntity> entityHolder = Optional.absent();

	public void addNeighbor(Direction direction, FieldHolder fieldHolder) {
		neighbors.put(checkNotNull(direction), checkNotNull(fieldHolder));
	}

	public FieldHolder getNeighbor(Direction direction) {
		return neighbors.get(checkNotNull(direction,
				"Direction cannot be null."));
	}

	public boolean isPresent() {
		return entityHolder.isPresent();
	}

	public FieldEntity getEntity() {
		return entityHolder.get();
	}

	public void setFieldEntity(FieldEntity entity) {
		entityHolder = Optional.of(checkNotNull(entity,
				"FieldEntity cannot be null."));
	}

	public void clearField() {
		if (entityHolder.isPresent()) {
			entityHolder = Optional.absent();
		}
	}

}
