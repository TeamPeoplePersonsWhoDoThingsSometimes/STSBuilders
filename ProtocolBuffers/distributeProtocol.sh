protogen ./*.proto
protocs --java_out=./ ./*.proto
cp ./*.cs ./../repo/ProcGenRPG/ProcGenRPG/ProcGenRPG/Assets/Scripts/Model/ProtocolBuffers/
cp ./stsquestbuilder/protocolbuffers/*.java ./../Builders/STSQuestBuilder/src/stsquestbuilder/protocolbuffers/
rm ./*.cs
rm ./stsquestbuilder/protocolbuffers/*.java