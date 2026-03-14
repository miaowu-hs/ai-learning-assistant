export interface WrongKnowledgePointStat {
  knowledgePoint: string
  count: number
}

export interface LearningReport {
  documentCount: number
  qaCount: number
  practiceCount: number
  accuracy: number
  recentWrongKnowledgePoints: WrongKnowledgePointStat[]
  suggestions: string[]
}
