/**
 * This package defines a small hierarchy of classes related to managing contents provided by web services.
 * 
@startuml img/fr.cetelem.mobile.service.contentmanager.classDiagram.png
class AbstractContentManager {
  - List<OnContentRequestFinishedListener> listListeners 
  - List<Intent> listIntents
}
note right: Part of the Framework

interface OnContentRequestFinishedListener {
  + onRequestFinished(int requestId, int resultCode, Object result)
}

class ContentManager extends AbstractContentManager
note right: Part of the Application


AbstractContentManager "1" *-left- "many" OnContentRequestFinishedListener

@enduml
 */
package com.octo.android.rest.client.contentmanager.listener;