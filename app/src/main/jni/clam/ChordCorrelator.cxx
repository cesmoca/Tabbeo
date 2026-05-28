#include "ChordCorrelator.hxx"

#include <list>
#include <cmath>
#include <algorithm>
#include <sstream>

// #define VERBOSE

namespace Simac
{
#ifdef VERBOSE
  char sNormPCP[512];
#endif
  
  // Each number in the pattern indicates the root in the chord
  // 1-5 are the 3 main notes that forms the chord.
  // 6, 7 and 8 are the fourth component (Sixth, Seventh and MajorSeventh respectively)
  //  This is not used in the algorithm, it just makes it more beautiful and easy to understand the nature of the chord
  /*static*/ const std::map<ChordCorrelator::Mode, ChordCorrelator::ModePattern>& ChordCorrelator::getModePatterns(){
    static const std::map<Mode, ModePattern> modePatterns =
      {	
	// Mode       Pattern          chordNotes / notChordNotes
	//{Fifth,      {{1,0,0,0,0,0,0,5,0,0,0,0}, 2, 10}},// 2 - C5
	  
	{Major,      {{1,0,0,0,3,0,0,5,0,0,0,0}, 3, 9}}, // 3
	{Minor,      {{1,0,0,3,0,0,0,5,0,0,0,0}, 3, 9}}, // 3
	{Suspended2, {{1,0,2,0,0,0,0,5,0,0,0,0}, 3, 9}}, // 3
	{Suspended4, {{1,0,0,0,0,4,0,5,0,0,0,0}, 3, 9}}, // 3
	{Augmented,  {{1,0,0,0,3,0,0,0,5,0,0,0}, 3, 9}}, // 3
	{Diminished, {{1,0,0,3,0,0,5,0,0,0,0,0}, 3, 9}}, // 3

	{Sixth,      {{1,0,0,0,3,0,0,5,0,6,0,0}, 4, 8}}, // 4
	{Minor6,     {{1,0,0,3,0,0,0,5,0,6,0,0}, 4, 8}}, // 4
	{Seventh,    {{1,0,0,0,3,0,0,5,0,0,7,0}, 4, 8}}, // 4
	{Minor7,     {{1,0,0,3,0,0,0,5,0,0,7,0}, 4, 8}}, // 4
	{Major7,     {{1,0,0,0,3,0,0,5,0,0,0,8}, 4, 8}}, // 4
	{MinorMajor7,{{1,0,0,3,0,0,0,5,0,0,0,8}, 4, 8}}, // 4
	{Diminished7,{{1,0,0,3,0,0,5,0,0,0,7,0}, 4, 8}}  // 4
      };
      
    if(modePatterns.size() != Diminished7 + 1){
      LOGE("The length of the chord modes do not match  %d vs %d", (int)(modePatterns.size()), Diminished7 + 1);
      throw "The length of the chord modes do not match";
    }
      
    return modePatterns;
  }

  ChordCorrelator::ChordCorrelator(int correlationThreshold, double componentThreshold): _correlationThreshold(correlationThreshold), _componentThreshold(componentThreshold)
  {
    const auto& patternsToDetect = getModePatterns();
      
    for (auto pair = patternsToDetect.begin(); pair != patternsToDetect.end(); ++pair)
      {
	const Mode& mode = pair->first;
	const ModePattern& modePattern = pair->second;
	
	for (unsigned pitch = G; pitch < Gb; ++pitch)
	  {
	    ChordPattern chordPattern(static_cast<Pitch>(pitch), mode, &modePattern);

	    for (unsigned i= chordPattern.chord.pitch; i<12; ++i)
	      chordPattern.pattern[i] = modePattern.templatePattern[i-chordPattern.chord.pitch];

	    for (unsigned i=0; i<chordPattern.chord.pitch; ++i)
	      chordPattern.pattern[i] = modePattern.templatePattern[12+i-chordPattern.chord.pitch];

	    _chordPatterns.push_back(chordPattern);
	  }
      }
  }
   
  
  void ChordCorrelator::doIt(const std::vector<double>& pcp)
  {
    _maxPCP = 0;
    
    // Let's find the max intensity of the pcp
    for(unsigned pitch = 0; pitch<12; ++pitch){
      if(pcp[pitch] > _maxPCP) _maxPCP = pcp[pitch];
    }


#ifdef VERBOSE    
    //LOGV("PCP(MAX:%f): G[%.2f] G#[%.2f], A[%.2f] A#[%.2f] B[%.2f] C[%.2f] C#[%.2f] D[%.2f] D#[%.2f] E[%.2f] F[%.2f] F#[%.2f]", _maxPCP,
    //	 pcp[0],pcp[1],pcp[2],pcp[3],pcp[4],pcp[5],pcp[6],pcp[7],pcp[8],pcp[9],pcp[10],pcp[11]);

    sprintf(sNormPCP, "NORPCP: G[%.2f] G#[%.2f], A[%.2f] A#[%.2f] B[%.2f] C[%.2f] C#[%.2f] D[%.2f] D#[%.2f] E[%.2f] F[%.2f] F#[%.2f]",
	    pcp[0]/_maxPCP,pcp[1]/_maxPCP,pcp[2]/_maxPCP,pcp[3]/_maxPCP,pcp[4]/_maxPCP,pcp[5]/_maxPCP,pcp[6]/_maxPCP,pcp[7]/_maxPCP,pcp[8]/_maxPCP,pcp[9]/_maxPCP,pcp[10]/_maxPCP,pcp[11]/_maxPCP);

    //LOGV("%s", sNormPCP);
#endif

    for (ChordPattern& chordPattern : _chordPatterns)
      {
	double correlation = 0.0; // This correlation is in the range [0, 12.0] of doubles
	
	for (unsigned pitch=0; pitch<12; pitch++)
	  {
	    double normalizedPCP = pcp[pitch] / _maxPCP;

	    if(normalizedPCP < _componentThreshold) normalizedPCP = 0;
	    
	    if(chordPattern.pattern[pitch] == 0) // Not chord notes. Adding points inversely proportional to the pcp
	      {
		correlation += 1 - normalizedPCP;
	      }
	    else // Chord note. Adding points directly proportional to the pcp
	      {
		correlation += normalizedPCP;
	      }
	  }
	
	// Now we bring it to a [0, 120] range of integers (to avoid unaccuracies)
	chordPattern.correlation = (int)(correlation*10);
	
      }
  }
  
  // Returns those chords that are identical (every chord has its equivalences)
  std::vector<ChordCorrelator::Chord*> ChordCorrelator::getDetectedChords(){

    // We sort them by correlation
    std::sort(_chordPatterns.begin(), _chordPatterns.end());

    int maxCorrelation = _chordPatterns[0].correlation;
    int underMaxCorrelation = 0;
    ChordPattern* underMaxChord;
        
    // Do not even meet the minimun required for a decent correlation
    if (maxCorrelation < _correlationThreshold) return {};
    
    std::vector<ChordCorrelator::Chord*> chordCandidates;

#ifdef VERBOSE
    std::stringstream ss;
    ss << ">> Detected: ";
#endif
    
    for(ChordPattern& chordPattern : _chordPatterns)
      {
	if(chordPattern.correlation == maxCorrelation)
	  {
	    chordCandidates.push_back(&chordPattern.chord);
	    
#ifdef VERBOSE
	    ss << " [" << chordPattern.correlation << "]" << chordPattern.chord.toString();
#endif
	  }
	else
	  {
	    underMaxCorrelation = chordPattern.correlation;
	    underMaxChord = &chordPattern;
	    break;
	  }
      }

#ifdef VERBOSE    
    LOGV("%s", sNormPCP);
    LOGV("%s", ss.str().c_str());
    
    // If the second approximation is super close, we (might) crash. To understand what is going on
    if(maxCorrelation*0.95 < underMaxCorrelation)
      {
	LOGV("%s", sNormPCP);
	LOGV("Second candidate too close: [%d] %s [%d]: %s", maxCorrelation, chordCandidates[0]->toString().c_str(), underMaxCorrelation, underMaxChord->chord.toString().c_str());
	throw "Second candidate too close";
      }
#endif    
    
    return chordCandidates;
  }

  /*static*/ const std::string& ChordCorrelator::getPitchName(const ChordCorrelator::Pitch& pitch){
    static const std::vector<std::string> pitchNames = {"G","G#","A","A#","B","C","C#","D","D#","E","F","F#"};
    
    return pitchNames[pitch];
  }
    
  /*static*/ const std::string& ChordCorrelator::getModeName(const ChordCorrelator::Mode& mode){
    static const std::vector<std::string> modeNames = {//"Fifth", // 2 notes
						       "Major", "Minor", "Suspended2", "Suspended4", "Augmented", "Diminished",
						       "6", "Minor6", "7", "Minor7", "Major7", "MinorMajor7", "Diminished7"}; // 3 notes
    
    if(modeNames.size() != Diminished7 + 1){
      LOGE("The length of the modeNames do not match %d vs %d", (int)(modeNames.size()), Diminished7+1);
      throw "The length of the modeNames do not match";
    }
    
    return modeNames[mode];
  }
} // namespace Simac


