# Events Usage

The plugin have multiple events to interact with distinct tags-aspects in runtime.
* `TagCreateEvent` called when a player creates a tag.
* `TagDeleteEvent` called when a tag is deleted.
* `TagSelectEvent` called when a tag is selected by the player.
* `TagUnselectEvent` called when the player unselects' current tag.
* `TagPropertyChangeEvent` called when a tag's prefix or suffix is about to be modified.

Both events provides methods both for involved-player, the involved-tag's id, or tags's properties, you can check the event-classes
to view what methods are available, as well, these methods are documented for better understanding.

Only the `TagPropertyChangeEvent` event can be cancelled, this means that the plugin will skip any modification-apply
operation for the current tag.
